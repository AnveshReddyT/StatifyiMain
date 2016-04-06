package statifyi.com.statifyi.service;

import android.app.IntentService;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;
import java.util.List;

import retrofit.Response;
import rx.functions.Action1;
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

    private UserAPIService userAPIService;
    private DataUtils dataUtils;

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
            sendRegistrationToServer(token);

            subscribeTopics(token);

        } catch (Exception e) {
            Log.d("STAT", "Failed to complete token refresh", e);
            GCMUtils.sendGcmToServerStatus(this, false);
        }
    }

    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
        if (dataUtils == null) {
            dataUtils = new DataUtils(PreferenceManager.getDefaultSharedPreferences(this));
        }
        GCMRequest request = new GCMRequest();
        request.setGcmId(token);
        request.setMobile(dataUtils.getMobileNumber());
        userAPIService.registerGCM(request).subscribe(new Action1<Response<Void>>() {
            @Override
            public void call(Response<Void> voidResponse) {
                Log.d("STAT", voidResponse.message() + " GCM send id");
            }
        });
        GCMUtils.sendGcmToServerStatus(this, true);
    }

    private void subscribeTopics(String token) throws IOException {
        List<String> contacts = Utils.getPhoneNumbersFromContacts(this);
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        int count = 0;
//        for (String topic : contacts) {
//            String tendigitNum = Utils.getLastTenDigits(topic);
//            if (tendigitNum != null) {
                try {
                    pubSub.subscribe(token, "/topics/" + "9676316323", null);
                    Log.d("STAT", "Subscribed to : " + "9676316323");
                    count++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
//            }
//        }
        Log.d("STAT", "Subscribed to " + count + " topics");
    }
}
