package statifyi.com.statifyi.service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import statifyi.com.statifyi.api.service.UserAPIService;
import statifyi.com.statifyi.utils.FCMUtils;
import statifyi.com.statifyi.utils.NetworkUtils;

/**
 * Created by KT on 02/04/16.
 */
public class FCMInstanceIDService extends FirebaseInstanceIdService {

    public static final String BROADCAST_ACTION_FCM_REGISTER = "statifyi.broadcast.fcm_register";

    private UserAPIService userAPIService;

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    @Override
    public void onTokenRefresh() {
        if (userAPIService == null) {
            userAPIService = NetworkUtils.provideUserAPIService(this);
        }
        try {
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            Log.i("STAT", "FCM Registration Token: " + refreshedToken);
            FCMUtils.storeRegistrationId(this, refreshedToken);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("STAT", "Failed to complete token refresh", e);
            FCMUtils.sendFcmToServerStatus(this, false);
        }
    }
}