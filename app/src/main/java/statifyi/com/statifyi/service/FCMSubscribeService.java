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
import statifyi.com.statifyi.api.service.FCMAPIService;
import statifyi.com.statifyi.utils.DataUtils;
import statifyi.com.statifyi.utils.FCMUtils;
import statifyi.com.statifyi.utils.NetworkUtils;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class FCMSubscribeService extends IntentService {

    public static final String[] CUSTOM_TOPICS = {"", "-customCall"};
    private static final int NOTIFICATION_ID = 101;
    private static boolean isRunning = false;

    private String mobile;

    public FCMSubscribeService() {
        super("FCMSubscribeService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!isRunning) {
            isRunning = true;
            mobile = DataUtils.getMobileNumber(this);
            try {
                FCMAPIService FCMAPIService = NetworkUtils.provideFCMAPIService(this, false);
                Response<ResponseBody> response = FCMAPIService.getFcmInfo(FCMUtils.getRegistrationId(this)).execute();
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    JSONObject fcmInfo = new JSONObject(new String(responseBody.bytes()));
                    if (fcmInfo.has("rel")) {
                        JSONObject fcmRelations = fcmInfo.getJSONObject("rel");
                        if (fcmRelations != null && fcmRelations.has("topics")) {
                            JSONObject fcmTopics = fcmRelations.getJSONObject("topics");
                            if (fcmTopics != null) {
                                Iterator<String> topics = fcmTopics.keys();
                                Set<String> fcmTopicSet = new HashSet<>();
                                while (topics.hasNext()) {
                                    String mTopic = topics.next();
                                    fcmTopicSet.add(mTopic);
                                }
                                verifySubscriptions(fcmTopicSet);
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

    private void verifySubscriptions(Set<String> fcmTopicSet) {
//        Set<String> contactList = new HashSet<>();
//        contactList.addAll(Utils.get10DigitPhoneNumbersFromContacts(this));

        Set<String> subscribeList = new HashSet<>();
//        subscribeList.addAll(contactList);
//        subscribeList.removeAll(fcmTopicSet);
        for (String customTopic : CUSTOM_TOPICS) {
            if (!fcmTopicSet.contains(mobile + customTopic)) {
                subscribeList.add(mobile + customTopic);
            }
        }

        Log.d("TAG_STAT", "Subscribed to : " + fcmTopicSet.size());
//        Log.d("TAG_STAT", "Available : " + contactList.size());
        Log.d("TAG_STAT", "Remaining: " + subscribeList.size());
        subscribe(subscribeList);

        fcmTopicSet.addAll(subscribeList);
        Log.d("TAG_STAT", "Subscribed to : " + fcmTopicSet.size());

//        fcmTopicSet.removeAll(contactList);
//        for (String customTopic : CUSTOM_TOPICS) {
//            if (fcmTopicSet.contains(mobile + customTopic)) {
//                fcmTopicSet.remove(mobile + customTopic);
//            }
//        }
//        Log.d("TAG_STAT", "to delete: " + fcmTopicSet.size());
//        unSubscribe(fcmTopicSet);
    }

    private void subscribe(Set<String> subscribeList) {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : subscribeList) {
            try {
                pubSub.subscribe(FCMUtils.getRegistrationId(this), FCMListenerService.TOPICS + topic, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void unSubscribe(Set<String> unSubscribeList) {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : unSubscribeList) {
            try {
                pubSub.unsubscribe(FCMUtils.getRegistrationId(this), FCMListenerService.TOPICS + topic);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
