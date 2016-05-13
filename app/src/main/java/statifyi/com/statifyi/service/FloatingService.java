package statifyi.com.statifyi.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import statifyi.com.statifyi.R;
import statifyi.com.statifyi.api.model.CustomCall;
import statifyi.com.statifyi.api.model.StatusResponse;
import statifyi.com.statifyi.api.model.User;
import statifyi.com.statifyi.api.service.UserAPIService;
import statifyi.com.statifyi.data.DBHelper;
import statifyi.com.statifyi.listener.CustomPhoneStateListener;
import statifyi.com.statifyi.model.CallLog;
import statifyi.com.statifyi.utils.NetworkUtils;
import statifyi.com.statifyi.utils.StatusUtils;
import statifyi.com.statifyi.utils.Utils;
import statifyi.com.statifyi.widget.FloatingPopup;

public class FloatingService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static CustomCall customCall;
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
    private GoogleApiClient mApiClient;
    private ContentObserver mObserver = new ContentObserver(new Handler()) {

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            CallLog mCallLog = Utils.getCallLogs(FloatingService.this).get(0);
            if (customCall != null) {
                dbHelper.insertOrUpdateCallLog(mCallLog.getDate(), customCall.getMessage());
                customCall = null;
            }
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
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
        getContentResolver().registerContentObserver(android.provider.CallLog.Calls.CONTENT_URI, true, mObserver);
        registerReceiver(OutgoingCallReceiver, new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL));
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mApiClient.connect();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
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
        getContentResolver().unregisterContentObserver(mObserver);
        TelephonyMgr.listen(listener, PhoneStateListener.LISTEN_NONE);
        stopActivityRecognitionUpdates();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    private void setExistingStatus(String mobile, String contactName, User mUser) {
        floatingPopup.setMessage(contactName + " is " + mUser.getStatus().toUpperCase());
        floatingPopup.setMobile(Utils.getLastTenDigits(mobile));
        String updatedTime = Utils.timeAgo(mUser.getUpdated());
        if (mUser.getAutoStatus() > 0) {
            updatedTime += " [Auto-status]";
        }
        floatingPopup.setTime(updatedTime);
        floatingPopup.setStatusIcon(Utils.getDrawableResByName(FloatingService.this, mUser.getIcon()));
    }

    private void fetchStatus(final String phoneNumber) {
        final String tenDigitNumber = Utils.getLastTenDigits(phoneNumber);
        if (tenDigitNumber == null) {
            return;
        }
        final String mContactName = Utils.getContactName(FloatingService.this, tenDigitNumber);
        floatingPopup.show();
        if (customCall != null) {
            floatingPopup.setStatusLayoutColor(StatusUtils.getCustomCallLayoutColor(customCall.getMessage(), FloatingService.this));
        } else {
            floatingPopup.setStatusLayoutColor(StatusUtils.getCustomCallLayoutColor("", FloatingService.this));
        }
        User mUser = dbHelper.getUser(tenDigitNumber);
        if (mUser != null) {
            setExistingStatus(phoneNumber, mContactName, mUser);
        } else {
            floatingPopup.resetPopup();
            floatingPopup.setMobile(tenDigitNumber);
        }
        if (NetworkUtils.isOnline()) {
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
                            int autoStatus = s.getAutoStatus();
                            long time = s.getUpdatedTime();
//                        floatingPopup.setMobile(tenDigitNumber);
                            contactName = contactName.equals(phoneNumber) ? name : contactName;
                            if (status.isEmpty()) {
                                statusMessage = contactName + getResources().getString(R.string.status_not_set);
                            } else {
                                Utils.saveUserStatusToLocal(status, name, icon, tenDigitNumber, autoStatus, time, dbHelper);
                                statusMessage = contactName + " is " + status;
                            }
                            String updatedTime = Utils.timeAgo(s.getUpdatedTime());
                            if (s.getAutoStatus() > 0) {
                                updatedTime += " [Auto-status]";
                            }
                            floatingPopup.setPopupMenu(false);
                            floatingPopup.setTime(updatedTime);
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
//                    floatingPopup.setMobile(tenDigitNumber);
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
        } else {
//            Utils.showToast(FloatingService.this, "No Internet!");
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getBoolean(getString(R.string.key_auto_status), false)
                && sharedPreferences.getBoolean(getString(R.string.key_driving_mode), false)) {
            startActivityRecognitionUpdates();
        }

    }

    private void startActivityRecognitionUpdates() {
        if (mApiClient.isConnected()) {
            PendingIntent pendingIntent = getActivityRecognitionPendingIntent();
            ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mApiClient, 10 * 60 * 1000, pendingIntent);
        } else {
            Log.d("STAT", "API not connected");
        }
    }

    private void stopActivityRecognitionUpdates() {
        if (mApiClient.isConnected()) {
            PendingIntent pendingIntent = getActivityRecognitionPendingIntent();
            ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(mApiClient, pendingIntent);
        } else {
            Log.d("STAT", "API not connected");
        }
    }

    private PendingIntent getActivityRecognitionPendingIntent() {
        Intent intent = new Intent(this, ActivityRecognizeService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("STAT", "Connection Failed");
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (getString(R.string.key_auto_status).equals(key)) {
            if (sharedPreferences.getBoolean(getString(R.string.key_auto_status), false)) {
                if (sharedPreferences.getBoolean(getString(R.string.key_driving_mode), false)) {
                    startActivityRecognitionUpdates();
                }
            } else {
                stopActivityRecognitionUpdates();
            }
        } else if (getString(R.string.key_driving_mode).equals(key)) {
            if (sharedPreferences.getBoolean(getString(R.string.key_driving_mode), false)) {
                if (sharedPreferences.getBoolean(getString(R.string.key_auto_status), false)) {
                    startActivityRecognitionUpdates();
                }
            } else {
                stopActivityRecognitionUpdates();
            }
        }
    }
}