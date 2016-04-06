package statifyi.com.statifyi.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import retrofit.Response;
import rx.functions.Action1;
import statifyi.com.statifyi.R;
import statifyi.com.statifyi.api.model.StatusResponse;
import statifyi.com.statifyi.api.model.User;
import statifyi.com.statifyi.api.service.UserAPIService;
import statifyi.com.statifyi.data.DBHelper;
import statifyi.com.statifyi.listener.CustomPhoneStateListener;
import statifyi.com.statifyi.utils.NetworkUtils;
import statifyi.com.statifyi.utils.Utils;
import statifyi.com.statifyi.widget.FloatingPopup;

public class FloatingService extends Service {

    private UserAPIService userAPIService;

    private DBHelper dbHelper;

    private TelephonyManager TelephonyMgr;

    private FloatingPopup floatingPopup;

    private BroadcastReceiver OutgoingCallReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("STATIFYI", " OUT GOING CALL RECEIVER ");
            String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            int length = phoneNumber.length();
            phoneNumber = length > 10 ? phoneNumber.substring(length - 10, length) : phoneNumber;
            floatingPopup.show();
            final String contactName = Utils.getContactName(FloatingService.this, phoneNumber);
            fetchStatus(phoneNumber, contactName);
        }
    };
    private CustomPhoneStateListener listener;

    /**
     * @param intent
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        userAPIService = NetworkUtils.provideUserAPIService(this);
        dbHelper = new DBHelper(this);
        floatingPopup = new FloatingPopup(this);
        TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new CustomPhoneStateListener(this, floatingPopup);
        TelephonyMgr.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        registerReceiver(OutgoingCallReceiver, new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(OutgoingCallReceiver);
        TelephonyMgr.listen(listener, PhoneStateListener.LISTEN_NONE);
    }

    private void fetchStatus(final String phoneNumber, final String contactName) {
        userAPIService.getUserStatus(phoneNumber).subscribe(new Action1<Response<StatusResponse>>() {
            @Override
            public void call(Response<StatusResponse> response) {
                if (floatingPopup != null && floatingPopup.isShowing()) {
                    String statusMessage;
                    if (response.code() == 200) {
                        StatusResponse s = response.body();
                        String status = s.getStatus().toUpperCase();
                        String icon = s.getIcon();
                        long time = s.getUpdatedTime().getTime();
                        Log.d("STAT", s.toString() + "   ===   " + time);
                        if (status.isEmpty()) {
                            statusMessage = contactName + getResources().getString(R.string.status_not_set);
                        } else {
                            Utils.saveUserStatusToLocal(status, icon, phoneNumber, time, dbHelper);
                            statusMessage = contactName + " is " + status;
                        }
                        floatingPopup.setPopupMenu(false);
                        floatingPopup.setTime(Utils.timeAgo(s.getUpdatedTime().getTime()));
                        floatingPopup.setStatusIcon(Utils.getDrawableResByName(FloatingService.this, icon));
                    } else {
                        floatingPopup.setPopupMenu(true);
                        floatingPopup.setTime(null);
                        statusMessage = contactName + getResources().getString(R.string.status_user_not_found);
                        floatingPopup.setStatusIcon(R.drawable.ic_launcher);
                    }
                    floatingPopup.setMessage(statusMessage);
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
                String statusMessage;
                User user = dbHelper.getUser(phoneNumber);
                if (user != null) {
                    String status = user.getStatus().toUpperCase();
                    String icon = user.getIcon();
                    if (status.isEmpty()) {
                        statusMessage = contactName + getResources().getString(R.string.status_not_set);
                    } else {
                        statusMessage = contactName + " is " + status/* + "(" + Utils.timeAgo(s.getUpdatedTime()) + ")"*/;
                    }
                    floatingPopup.setPopupMenu(false);
                    floatingPopup.setTime(user.getUpdated() + "");
                    floatingPopup.setStatusIcon(Utils.getDrawableResByName(FloatingService.this, icon));
                } else {
                    floatingPopup.setTime(null);
                    statusMessage = getResources().getString(R.string.status_no_network);
                }
                floatingPopup.setMessage(statusMessage);
                floatingPopup.setStatusIcon(R.drawable.ic_launcher);
            }
        });
    }
}