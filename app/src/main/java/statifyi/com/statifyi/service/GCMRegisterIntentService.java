package statifyi.com.statifyi.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;
import java.util.List;
import java.util.Set;

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

    private static final int NOTIFICATION_ID = 101;
    private UserAPIService userAPIService;
    private DataUtils dataUtils;

    public GCMRegisterIntentService() {
        super("GCMRegisterIntentService");
    }

    public static void subscribeTopics(Context mContext, final String token) {
        try {
            NotificationManager mNotifyManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
            mBuilder.setContentTitle("Subscribing")
                    .setContentText("Subscribing in progress")
                    .setSmallIcon(R.drawable.ic_status);
            List<String> contacts = Utils.get10DigitPhoneNumbersFromContacts(mContext);
            final Set<String> topics = GCMUtils.getSubscriptions(mContext);
            final GcmPubSub pubSub = GcmPubSub.getInstance(mContext);
            for (int i = 0; i < contacts.size(); i++) {
                String topic = contacts.get(i);
                if (!topics.contains(topic)) {
                    if (topic != null) {
                        try {
                            mBuilder.setProgress(contacts.size(), i, false);
                            mNotifyManager.notify(NOTIFICATION_ID, mBuilder.build());
                            pubSub.subscribe(token, "/topics/" + topic, null);
                            topics.add(topic);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            GCMUtils.saveSubscriptions(mContext, topics);
            mBuilder.setContentText("Subscription complete")
                    .setProgress(0, 0, false);
            mNotifyManager.notify(NOTIFICATION_ID, mBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void unsubscribeTopic(final Context mContext, final String topic) {
        final GcmPubSub pubSub = GcmPubSub.getInstance(mContext);
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    pubSub.unsubscribe(GCMUtils.getRegistrationId(mContext), "/topics/" + topic);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
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

            subscribeTopics(this, token);

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
            }
        });
        GCMUtils.sendGcmToServerStatus(this, true);
    }
}
