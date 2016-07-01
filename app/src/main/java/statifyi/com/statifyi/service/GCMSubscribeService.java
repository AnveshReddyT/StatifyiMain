package statifyi.com.statifyi.service;

import android.app.IntentService;
import android.content.Intent;
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
import statifyi.com.statifyi.api.service.GCMAPIService;
import statifyi.com.statifyi.utils.DataUtils;
import statifyi.com.statifyi.utils.GCMUtils;
import statifyi.com.statifyi.utils.NetworkUtils;
import statifyi.com.statifyi.utils.Utils;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class GCMSubscribeService extends IntentService {

    public static final String[] CUSTOM_TOPICS = {"", "-customCall"};
    private static final int NOTIFICATION_ID = 101;
    private static boolean isRunning = false;

    private String mobile;

    public GCMSubscribeService() {
        super("GCMSubscribeService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!isRunning) {
            isRunning = true;
            mobile = DataUtils.getMobileNumber(this);
            try {
                GCMAPIService gcmapiService = NetworkUtils.provideGCMAPIService(this, false);
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
                                    String mTopic = topics.next();
                                    gcmTopicSet.add(mTopic);
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

    private boolean isCustomTopic(String topic) {
        for (String str : CUSTOM_TOPICS) {
            if ((mobile + str).equals(topic)) {
                return true;
            }
        }
        return false;
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
//        subscribeList.addAll(contactList);
//        subscribeList.removeAll(gcmTopicSet);
        for (String customTopic : CUSTOM_TOPICS) {
            if (!gcmTopicSet.contains(mobile + customTopic)) {
                subscribeList.add(mobile + customTopic);
            }
        }

        Log.d("TAG_STAT", "Subscribed to : " + gcmTopicSet.size());
//        Log.d("TAG_STAT", "Available : " + contactList.size());
        Log.d("TAG_STAT", "Remaining: " + subscribeList.size());
        subscribe(subscribeList);

        gcmTopicSet.addAll(subscribeList);
        Log.d("TAG_STAT", "Subscribed to : " + gcmTopicSet.size());

//        gcmTopicSet.removeAll(contactList);
//        for (String customTopic : CUSTOM_TOPICS) {
//            if (gcmTopicSet.contains(mobile + customTopic)) {
//                gcmTopicSet.remove(mobile + customTopic);
//            }
//        }
//        Log.d("TAG_STAT", "to delete: " + gcmTopicSet.size());
//        unSubscribe(gcmTopicSet);
    }

    private void subscribe(Set<String> subscribeList) {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : subscribeList) {
            try {
                pubSub.subscribe(GCMUtils.getRegistrationId(this), GCMIntentService.TOPICS + topic, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void unSubscribe(Set<String> unSubscribeList) {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : unSubscribeList) {
            try {
                pubSub.unsubscribe(GCMUtils.getRegistrationId(this), GCMIntentService.TOPICS + topic);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
