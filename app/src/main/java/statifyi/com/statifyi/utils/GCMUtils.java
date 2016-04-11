package statifyi.com.statifyi.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by KT on 02/04/16.
 */
public class GCMUtils {

    private static final String GCM_PREF = "gcm_pref";
    private static final String REG_ID = "regId";
    private static final String GCM_SEND_SERVER = "gcm_send_server";
    private static final String GCM_TOPICS = "gcm_topics";
    private static final String APP_VERSION = "appVersion";

    public static String getRegistrationId(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(GCM_PREF, Context.MODE_PRIVATE);
        String registrationId = prefs.getString(REG_ID, "");
        if (registrationId.isEmpty()) {
            return "";
        }
        int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            return "";
        }
        return registrationId;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = context.getSharedPreferences(GCM_PREF, Context.MODE_PRIVATE);
        int appVersion = getAppVersion(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(REG_ID, regId);
        editor.putInt(APP_VERSION, appVersion);
        editor.apply();
    }

    public static void sendGcmToServerStatus(Context context, boolean status) {
        final SharedPreferences prefs = context.getSharedPreferences(GCM_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(GCM_SEND_SERVER, status);
        editor.apply();
    }

    public static boolean getGcmToServerStatus(Context context, boolean status) {
        final SharedPreferences prefs = context.getSharedPreferences(GCM_PREF, Context.MODE_PRIVATE);
        return prefs.getBoolean(GCM_SEND_SERVER, false);
    }

    public static void saveSubscriptions(Context mContext, Set topics) {
        final SharedPreferences prefs = mContext.getSharedPreferences(GCM_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(GCM_TOPICS, topics);
        editor.apply();
    }

    public static void deleteSubscriptions(final Context mContext) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                List<String> currentContacts = Utils.get10DigitPhoneNumbersFromContacts(mContext);
                List<String> savedTopics = new ArrayList<>();
                final SharedPreferences prefs = mContext.getSharedPreferences(GCM_PREF, Context.MODE_PRIVATE);
                savedTopics.addAll(prefs.getStringSet(GCM_TOPICS, new HashSet<String>()));
                final GcmPubSub pubSub = GcmPubSub.getInstance(mContext);
                for (int i = 0; i < savedTopics.size(); i++) {
                    final String topic = savedTopics.get(i);
                    if (!currentContacts.contains(topic)) {
                        savedTopics.remove(topic);
                        try {
                            pubSub.unsubscribe(getRegistrationId(mContext), "/topics/" + topic);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                Set<String> topics = new HashSet<>();
                topics.addAll(savedTopics);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putStringSet(GCM_TOPICS, topics);
                editor.apply();
                return null;
            }
        }.execute();

    }

    public static void makeSubscriptions(final Context mContext) {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                List<String> currentContacts = Utils.get10DigitPhoneNumbersFromContacts(mContext);
                List<String> savedTopics = new ArrayList<>();
                SharedPreferences prefs = mContext.getSharedPreferences(GCM_PREF, Context.MODE_PRIVATE);
                savedTopics.addAll(prefs.getStringSet(GCM_TOPICS, new HashSet<String>()));
                GcmPubSub pubSub = GcmPubSub.getInstance(mContext);
                Log.d("STAT", currentContacts.size() + " --- " + savedTopics.size());
                for (int i = 0; i < currentContacts.size(); i++) {
                    String contact = currentContacts.get(i);
                    if (!savedTopics.contains(contact)) {
                        savedTopics.add(contact);
                        try {
                            pubSub.subscribe(getRegistrationId(mContext), "/topics/" + contact, null);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                Set<String> topics = new HashSet<>();
                topics.addAll(savedTopics);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putStringSet(GCM_TOPICS, topics);
                editor.apply();
                return null;
            }
        }.execute();
    }

    public static Set<String> getSubscriptions(Context mContext) {
        final SharedPreferences prefs = mContext.getSharedPreferences(GCM_PREF, Context.MODE_PRIVATE);
        return prefs.getStringSet(GCM_TOPICS, new HashSet<String>());
    }

    public static boolean isAlreadySubscribed(Context mContext, String topic) {
        final SharedPreferences prefs = mContext.getSharedPreferences(GCM_PREF, Context.MODE_PRIVATE);
        Set<String> stringSet = prefs.getStringSet(GCM_TOPICS, null);
        return stringSet != null && stringSet.contains(topic);
    }
}
