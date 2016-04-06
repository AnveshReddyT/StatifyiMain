package statifyi.com.statifyi.listener;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import statifyi.com.statifyi.api.model.CustomCall;
import statifyi.com.statifyi.api.service.UserAPIService;
import statifyi.com.statifyi.data.DBHelper;
import statifyi.com.statifyi.utils.NetworkUtils;
import statifyi.com.statifyi.utils.Utils;
import statifyi.com.statifyi.widget.FloatingPopup;

/**
 * Created by KT on 9/27/15.
 */
public class CustomPhoneStateListener extends PhoneStateListener {

    private UserAPIService userAPIService;

    private Context mContext;

    private FloatingPopup floatingPopup;

    private int lastState;

    private DBHelper dbHelper;

    public CustomPhoneStateListener(Context mContext, FloatingPopup floatingPopup) {
        this.mContext = mContext;
        this.floatingPopup = floatingPopup;
        userAPIService = NetworkUtils.provideUserAPIService(mContext);
        dbHelper = new DBHelper(mContext);
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
                    Log.d("STAT", customCall + "  custom call ");
                    if (customCall != null) {
                        final String contactName = Utils.getContactName(mContext, incomingNumber);
                        floatingPopup.show();
                        floatingPopup.setPopupMenu(false);
                        floatingPopup.setTime("from " + contactName);
                        floatingPopup.setStatusIcon(Utils.getDrawableResByName(mContext, customCall.getMessage()));
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
                break;
        }
        lastState = state;
    }

}
