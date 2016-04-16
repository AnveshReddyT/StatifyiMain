package statifyi.com.statifyi.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.squareup.okhttp.ResponseBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import retrofit.Response;
import statifyi.com.statifyi.R;
import statifyi.com.statifyi.api.service.GCMAPIService;
import statifyi.com.statifyi.utils.GCMUtils;
import statifyi.com.statifyi.utils.NetworkUtils;
import statifyi.com.statifyi.utils.Utils;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class GCMSubscribeService extends IntentService {

    private static final int NOTIFICATION_ID = 101;

    private static boolean isRunning = false;

    public GCMSubscribeService() {
        super("GCMSubscribeService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!isRunning) {
            isRunning = true;
            try {
                GCMAPIService gcmapiService = NetworkUtils.provideGCMAPIService(this);
                Response<ResponseBody> response = gcmapiService.getGcmInfo(GCMUtils.getRegistrationId(this)).execute();
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    JSONObject gcmInfo = new JSONObject(new String(responseBody.bytes()));
                    if (gcmInfo.has("rel")) {
                        JSONObject gcmRelations = gcmInfo.getJSONObject("rel");
                        if (gcmRelations != null && gcmRelations.has("topics")) {
                            JSONObject gcmTopics = gcmRelations.getJSONObject("topics");
                            if (gcmTopics != null) {
                                Iterator<String> topics = gcmTopics.keys();
                                Set<String> gcmTopicSet = new HashSet<>();
                                while (topics.hasNext()) {
                                    gcmTopicSet.add(topics.next());
                                }
                                verifySubscriptions(gcmTopicSet);
                            }
                        }
                    } else {
                        verifySubscriptions(new HashSet<String>());
                    }
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        super.onDestroy();
    }

    private void verifySubscriptions(Set<String> gcmTopicSet) {
        Set<String> contactList = new HashSet<>();
        contactList.addAll(Utils.get10DigitPhoneNumbersFromContacts(this));

        Set<String> subscribeList = new HashSet<>();
        subscribeList.addAll(contactList);
        subscribeList.removeAll(gcmTopicSet);

        Log.d("STAT", "Subscribed to : " + gcmTopicSet.size());
        Log.d("STAT", "Available : " + contactList.size());
        Log.d("STAT", "Remaining: " + subscribeList.size());
        subscribe(subscribeList, contactList.size());

        gcmTopicSet.addAll(subscribeList);
        Log.d("STAT", "Subscribed to : " + gcmTopicSet.size());

        contactList.removeAll(gcmTopicSet);
        Log.d("STAT", "to delete: " + contactList.size());
        unSubscribe(contactList);
    }

    private void subscribe(Set<String> subscribeList, int totalCount) {
        NotificationManager mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle(getResources().getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_status);
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        int count = totalCount - subscribeList.size();
        for (String topic : subscribeList) {
            try {
                mBuilder.setContentText("Subscription in progress ( " + (count * 100 / totalCount) + "% )")
                        .setProgress(totalCount, count, false);
                mNotifyManager.notify(NOTIFICATION_ID, mBuilder.build());
                pubSub.subscribe(GCMUtils.getRegistrationId(this), "/topics/" + topic, null);
                count++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mNotifyManager.cancel(NOTIFICATION_ID);
    }

    private void unSubscribe(Set<String> unSubscribeList) {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : unSubscribeList) {
            try {
                pubSub.unsubscribe(GCMUtils.getRegistrationId(this), topic);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
