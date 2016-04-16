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
import statifyi.com.statifyi.api.model.CustomCall;
import statifyi.com.statifyi.api.model.StatusRequest;
import statifyi.com.statifyi.api.service.UserAPIService;
import statifyi.com.statifyi.data.DBHelper;
import statifyi.com.statifyi.model.CallLog;
import statifyi.com.statifyi.utils.DataUtils;
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
                if (lastState == TelephonyManager.CALL_STATE_IDLE) {
                    // Incolimg call
                    CustomCall customCall = dbHelper.getCustomCall(Utils.getLastTenDigits(incomingNumber));
                    if (customCall != null) {
                        customMessage = customCall.getMessage();
                        final String contactName = Utils.getContactName(mContext, incomingNumber);
                        floatingPopup.show();
                        floatingPopup.setPopupMenu(false);
                        floatingPopup.setTime("from " + contactName);
                        floatingPopup.setStatusIcon(StatusUtils.getCustomCallIcon(customCall.getMessage(), mContext));
                        floatingPopup.setMessage(customCall.getMessage());

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
                    Log.d("STAT", Utils.getCallLogs(mContext).get(0).toString());
                }
                break;
        }
        lastState = state;
    }


    private void updateStatus(final Context context, final String status) {
        StatusRequest request = new StatusRequest();
        request.setMobile(DataUtils.getMobileNumber(context));
        request.setStatus(status);
        request.setIcon(status);
        userAPIService.setUserStatus(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Response<Void> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    DataUtils.saveStatus(context, status);
                    int ico = Utils.getDrawableResByName(mContext, status);
                    DataUtils.saveIcon(context, ico);
                } else {
                }
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }
}
