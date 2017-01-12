package statifyi.com.statifyi.service;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import statifyi.com.statifyi.api.model.GCMRequest;
import statifyi.com.statifyi.api.service.UserAPIService;
import statifyi.com.statifyi.utils.DataUtils;
import statifyi.com.statifyi.utils.GCMUtils;
import statifyi.com.statifyi.utils.NetworkUtils;
import statifyi.com.statifyi.utils.Utils;

/**
 * Created by KT on 02/04/16.
 */
public class FCMInstanceIDService extends FirebaseInstanceIdService {

    public static final String BROADCAST_ACTION_GCM_REGISTER = "statifyi.broadcast.gcm_register";

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
            Log.i("STAT", "GCM Registration Token: " + refreshedToken);
            GCMUtils.storeRegistrationId(this, refreshedToken);
            sendRegistrationToServer(refreshedToken);
            if (!Utils.isMyServiceRunning(this, GCMSubscribeService.class)) {
                startService(new Intent(this, GCMSubscribeService.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("STAT", "Failed to complete token refresh", e);
            GCMUtils.sendGcmToServerStatus(this, false);
        }
    }

    private void sendRegistrationToServer(String token) {
        GCMRequest request = new GCMRequest();
        request.setGcmId(token);
        request.setMobile(DataUtils.getMobileNumber(this));

        userAPIService.registerGCM(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Response<Void> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    LocalBroadcastManager.getInstance(FCMInstanceIDService.this).sendBroadcast(new Intent(BROADCAST_ACTION_GCM_REGISTER));
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
        GCMUtils.sendGcmToServerStatus(this, true);
    }
}