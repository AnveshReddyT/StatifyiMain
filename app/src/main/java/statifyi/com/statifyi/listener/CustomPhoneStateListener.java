package statifyi.com.statifyi.listener;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import statifyi.com.statifyi.api.service.UserAPIService;
import statifyi.com.statifyi.utils.NetworkUtils;
import statifyi.com.statifyi.widget.FloatingPopup;

/**
 * Created by KT on 9/27/15.
 */
public class CustomPhoneStateListener extends PhoneStateListener {

    private UserAPIService userAPIService;

    private Context mContext;

    private FloatingPopup floatingPopup;

    private int lastState;

    public CustomPhoneStateListener(Context mContext, FloatingPopup floatingPopup) {
        this.mContext = mContext;
        this.floatingPopup = floatingPopup;
        userAPIService = NetworkUtils.provideUserAPIService(mContext);
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
