package statifyi.com.statifyi.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

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
            int length = phoneNumber.length();
            phoneNumber = length > 10 ? phoneNumber.substring(length - 10, length) : phoneNumber;
            final String contactName = Utils.getContactName(FloatingService.this, phoneNumber);
            fetchStatus(phoneNumber, contactName);
        }
    };
    private CustomPhoneStateListener listener;
    private int mContactCount;
    private ContentObserver mObserver = new ContentObserver(new Handler()) {

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            final int currentCount = getContactCount();
            if (currentCount < mContactCount) {
                // CONTACT DELETED.
                Log.d("Contact Service Deleted", currentCount + "");
                startService(new Intent(FloatingService.this, GCMSubscribeService.class));
            } else if (currentCount == mContactCount) {
                // CONTACT UPDATED.
                Log.d("Contact Service Updated", currentCount + "");
//                startService(new Intent(FloatingService.this, GCMSubscribeService.class));
            } else {
                // NEW CONTACT.
                Log.d("Contact Service Added", currentCount + "");
                startService(new Intent(FloatingService.this, GCMSubscribeService.class));
            }
            mContactCount = currentCount;
        }

    };

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
        mContactCount = getContactCount();
        this.getContentResolver().registerContentObserver(
                ContactsContract.Contacts.CONTENT_URI, true, mObserver);
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
        getContentResolver().unregisterContentObserver(mObserver);
    }

    private int getContactCount() {
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(
                    ContactsContract.Contacts.CONTENT_URI, null, null, null,
                    null);
            if (cursor != null) {
                return cursor.getCount();
            } else {
                return 0;
            }
        } catch (Exception ignore) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
    }

    private void setExistingStatus(String mobile, String contactName) {
        User mUser = dbHelper.getUser(mobile);
        if (mUser != null) {
            floatingPopup.setMobile(contactName);
            floatingPopup.setMessage(mUser.getStatus());
            floatingPopup.setTime(Utils.timeAgo(mUser.getUpdated()));
            floatingPopup.setStatusIcon(Utils.getDrawableResByName(FloatingService.this, mUser.getIcon()));
        }
    }

    private void fetchStatus(final String phoneNumber, final String contactName) {
        floatingPopup.show();
        setExistingStatus(phoneNumber, contactName);
        userAPIService.getUserStatus(phoneNumber).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Response<StatusResponse> response, Retrofit retrofit) {
                if (floatingPopup != null && floatingPopup.isShowing()) {
                    String statusMessage;
                    if (response.code() == 200) {
                        StatusResponse s = response.body();
                        String status = s.getStatus().toUpperCase();
                        String icon = s.getIcon();
                        long time = s.getUpdatedTime().getTime();
                        floatingPopup.setMobile(phoneNumber);
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

            @Override
            public void onFailure(Throwable t) {
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
                    floatingPopup.setMobile(phoneNumber);
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