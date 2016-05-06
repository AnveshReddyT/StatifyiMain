package statifyi.com.statifyi.listener;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import statifyi.com.statifyi.R;
import statifyi.com.statifyi.api.model.CustomCall;
import statifyi.com.statifyi.api.model.StatusResponse;
import statifyi.com.statifyi.api.service.UserAPIService;
import statifyi.com.statifyi.data.DBHelper;
import statifyi.com.statifyi.model.CallLog;
import statifyi.com.statifyi.utils.NetworkUtils;
import statifyi.com.statifyi.utils.StatusUtils;
import statifyi.com.statifyi.utils.Utils;
import statifyi.com.statifyi.widget.FloatingPopup;

/**
 * Created by KT on 9/27/15.
 */
public class CustomPhoneStateListener extends PhoneStateListener {

    private Context mContext;

    private FloatingPopup floatingPopup;

    private int lastState;

    private DBHelper dbHelper;

    private UserAPIService userAPIService;

    private String customMessage;

    private ContentObserver mObserver = new ContentObserver(new Handler()) {

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            CallLog mCallLog = Utils.getCallLogs(mContext).get(0);
            if (customMessage != null) {
                dbHelper.insertOrUpdateCallLog(mCallLog.getDate(), customMessage);
                customMessage = null;
                Log.d("STAT", dbHelper.getCustomCallLog(mCallLog.getDate()));
            }
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }
    };

    public CustomPhoneStateListener(Context mContext, FloatingPopup floatingPopup) {
        this.mContext = mContext;
        this.floatingPopup = floatingPopup;
        dbHelper = DBHelper.getInstance(mContext);
        userAPIService = NetworkUtils.provideUserAPIService(mContext);
        mContext.getContentResolver().registerContentObserver(android.provider.CallLog.Calls.CONTENT_URI, true, mObserver);
    }

    public void unregisterObserver() {
        mContext.getContentResolver().unregisterContentObserver(mObserver);
    }

    @Override
    public void onCallStateChanged(int state, final String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        if (lastState == state) {
            //No change, debounce extras
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                if (lastState == TelephonyManager.CALL_STATE_IDLE || lastState == TelephonyManager.CALL_STATE_OFFHOOK) {
                    // Incolimg call
                    String lastTenDigits = Utils.getLastTenDigits(incomingNumber);
                    CustomCall customCall = dbHelper.getCustomCall(lastTenDigits);
                    if (customCall != null) {
                        customMessage = customCall.getMessage();
                    }
                    String contactName = Utils.getContactName(mContext, incomingNumber);
                    floatingPopup.show();
                    floatingPopup.resetPopup();
                    floatingPopup.setPopupMenu(false);
                    floatingPopup.setMobile(lastTenDigits);
                    floatingPopup.setTime("from " + contactName);
                    String message = customCall == null ? mContext.getString(R.string.normal_call) : customCall.getMessage();
                    floatingPopup.setStatusIcon(StatusUtils.getCustomCallIcon(message, mContext));
                    floatingPopup.setStatusLayoutColor(StatusUtils.getCustomCallLayoutColor(message, mContext));
                    floatingPopup.setMessage(message);
                    dbHelper.deletedCustomCall(lastTenDigits);
                    if (contactName != null && contactName.equals(incomingNumber)) {
                        fetchStatus(incomingNumber);
                    }
                }
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                    // Out going call
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                if (floatingPopup != null) {
                    floatingPopup.destroy();
                }
                if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                    // Incoming Call ended
                }
                break;
        }
        lastState = state;
    }


    private void fetchStatus(final String phoneNumber) {
        final String tenDigitNumber = Utils.getLastTenDigits(phoneNumber);
        userAPIService.getUserStatus(tenDigitNumber).enqueue(new Callback<StatusResponse>() {

            @Override
            public void onResponse(Response<StatusResponse> response, Retrofit retrofit) {
                if (floatingPopup != null && floatingPopup.isShowing()) {
                    if (response.code() == 200) {
                        StatusResponse s = response.body();
                        String status = s.getStatus().toUpperCase();
                        String icon = s.getIcon();
                        String name = s.getName();
                        long time = s.getUpdatedTime();
                        Utils.saveUserStatusToLocal(status, name, icon, tenDigitNumber, time, dbHelper);
                        floatingPopup.setTime("from " + name);
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }
}
