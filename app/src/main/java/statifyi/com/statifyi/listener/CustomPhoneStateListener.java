package statifyi.com.statifyi.listener;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import statifyi.com.statifyi.R;
import statifyi.com.statifyi.api.model.CustomCall;
import statifyi.com.statifyi.data.DBHelper;
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

    public CustomPhoneStateListener(Context mContext, FloatingPopup floatingPopup) {
        this.mContext = mContext;
        this.floatingPopup = floatingPopup;
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
                    if (customCall != null) {
                        final String contactName = Utils.getContactName(mContext, incomingNumber);
                        floatingPopup.show();
                        floatingPopup.setPopupMenu(false);
                        floatingPopup.setTime("from " + contactName);
                        floatingPopup.setStatusIcon(getCustomCallIcon(customCall.getMessage()));
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

    private int getCustomCallIcon(String message) {
        if (mContext.getString(R.string.emergency_call).equals(message)) {
            return R.drawable.ic_call_emergency;
        } else if (mContext.getString(R.string.business_call).equals(message)) {
            return R.drawable.ic_call_business;
        } else if (mContext.getString(R.string.casual_call).equals(message)) {
            return R.drawable.ic_call_casual;
        } else {
            return R.drawable.ic_call_custom;
        }
    }
}
