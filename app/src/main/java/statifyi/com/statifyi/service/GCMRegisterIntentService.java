package statifyi.com.statifyi.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;
import java.util.List;

import statifyi.com.statifyi.R;
import statifyi.com.statifyi.utils.GCMUtils;
import statifyi.com.statifyi.utils.Utils;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class GCMRegisterIntentService extends IntentService {

    private static final String[] TOPICS = {"global"};

    public GCMRegisterIntentService() {
        super("GCMRegisterIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
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
        GCMUtils.sendGcmToServerStatus(this, true);
    }

    private void subscribeTopics(String token) throws IOException {
        List<String> contacts = Utils.getPhoneNumbersFromContacts(this);
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        int count = 0;
        for (String topic : contacts) {
            String tendigitNum = Utils.getLastTenDigits(topic);
            if (tendigitNum != null) {
                try {
                    pubSub.subscribe(token, "/topics/" + tendigitNum, null);
                    Log.d("STAT", "Subscribed to : " + tendigitNum);
                    count++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d("STAT", "Subscribed to " + count + " topics");
    }
}
