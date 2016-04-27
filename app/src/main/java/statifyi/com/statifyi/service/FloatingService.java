package statifyi.com.statifyi.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
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
            String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            fetchStatus(phoneNumber);
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
        dbHelper = DBHelper.getInstance(this);
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
        listener.unregisterObserver();
        TelephonyMgr.listen(listener, PhoneStateListener.LISTEN_NONE);
    }

    private void setExistingStatus(String mobile, String contactName, User mUser) {
        if (mUser != null) {
            floatingPopup.setMessage(contactName + " is " + mUser.getStatus().toUpperCase());
            floatingPopup.setMobile(Utils.getLastTenDigits(mobile));
            floatingPopup.setTime(Utils.timeAgo(mUser.getUpdated()));
            floatingPopup.setStatusIcon(Utils.getDrawableResByName(FloatingService.this, mUser.getIcon()));
        }
    }

    private void fetchStatus(final String phoneNumber) {
        final String tenDigitNumber = Utils.getLastTenDigits(phoneNumber);
        final String mContactName = Utils.getContactName(FloatingService.this, tenDigitNumber);
        floatingPopup.show();
        User mUser = dbHelper.getUser(tenDigitNumber);
        if (mUser != null) {
            setExistingStatus(phoneNumber, mContactName, mUser);
        } else {
            floatingPopup.resetPopup();
        }
        userAPIService.getUserStatus(tenDigitNumber).enqueue(new Callback<StatusResponse>() {

            String contactName = mContactName == null ? phoneNumber : mContactName;

            @Override
            public void onResponse(Response<StatusResponse> response, Retrofit retrofit) {
                if (floatingPopup != null && floatingPopup.isShowing()) {
                    String statusMessage;
                    if (response.code() == 200) {
                        StatusResponse s = response.body();
                        String status = s.getStatus().toUpperCase();
                        String icon = s.getIcon();
                        String name = s.getName();
                        long time = s.getUpdatedTime();
                        floatingPopup.setMobile(tenDigitNumber);
                        contactName = contactName.equals(phoneNumber) ? name : contactName;
                        if (status.isEmpty()) {
                            statusMessage = contactName + getResources().getString(R.string.status_not_set);
                        } else {
                            Utils.saveUserStatusToLocal(status, name, icon, tenDigitNumber, time, dbHelper);
                            statusMessage = contactName + " is " + status;
                        }
                        floatingPopup.setPopupMenu(false);
                        floatingPopup.setTime(Utils.timeAgo(s.getUpdatedTime()));
                        floatingPopup.setStatusIcon(Utils.getDrawableResByName(FloatingService.this, icon));
                    } else {
                        floatingPopup.setPopupMenu(true);
                        floatingPopup.setTime(null);
                        statusMessage = contactName + getResources().getString(R.string.status_user_not_found);
                        floatingPopup.setStatusIcon(R.drawable.ic_status);
                    }
                    floatingPopup.setMessage(statusMessage);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                String statusMessage;
                User user = dbHelper.getUser(tenDigitNumber);
                if (user != null) {
                    String status = user.getStatus().toUpperCase();
                    String icon = user.getIcon();
                    if (status.isEmpty()) {
                        statusMessage = contactName + getResources().getString(R.string.status_not_set);
                    } else {
                        statusMessage = contactName + " is " + status/* + "(" + Utils.timeAgo(s.getUpdatedTime()) + ")"*/;
                    }
                    floatingPopup.setMobile(tenDigitNumber);
                    floatingPopup.setPopupMenu(false);
                    floatingPopup.setTime(Utils.timeAgo(user.getUpdated()));
                    floatingPopup.setStatusIcon(Utils.getDrawableResByName(FloatingService.this, icon));
                } else {
                    floatingPopup.setTime(null);
                    statusMessage = getResources().getString(R.string.status_no_network);
                    floatingPopup.setStatusIcon(R.drawable.ic_status);
                }
                floatingPopup.setMessage(statusMessage);
            }
        });
    }
}