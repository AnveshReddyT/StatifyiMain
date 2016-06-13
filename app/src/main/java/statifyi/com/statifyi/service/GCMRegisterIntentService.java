package statifyi.com.statifyi.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import statifyi.com.statifyi.R;
import statifyi.com.statifyi.api.model.GCMRequest;
import statifyi.com.statifyi.api.service.UserAPIService;
import statifyi.com.statifyi.utils.DataUtils;
import statifyi.com.statifyi.utils.GCMUtils;
import statifyi.com.statifyi.utils.NetworkUtils;
import statifyi.com.statifyi.utils.Utils;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class GCMRegisterIntentService extends IntentService {

    public static final String BROADCAST_ACTION_GCM_REGISTER = "statifyi.broadcast.gcm_register";

    private UserAPIService userAPIService;

    public GCMRegisterIntentService() {
        super("GCMRegisterIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (userAPIService == null) {
            userAPIService = NetworkUtils.provideUserAPIService(this);
        }
        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.google_project_id), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.i("STAT", "GCM Registration Token: " + token);
            GCMUtils.storeRegistrationId(this, token);
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(BROADCAST_ACTION_GCM_REGISTER));
            sendRegistrationToServer(token);
            if (!Utils.isMyServiceRunning(this, GCMSubscribeService.class)) {
                startService(new Intent(this, GCMSubscribeService.class));
            }
        } catch (Exception e) {
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

            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
        GCMUtils.sendGcmToServerStatus(this, true);
    }
}
