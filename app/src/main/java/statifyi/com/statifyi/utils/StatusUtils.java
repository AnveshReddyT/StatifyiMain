package statifyi.com.statifyi.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.LinkedHashSet;
import java.util.Set;

import statifyi.com.statifyi.R;

/**
 * Created by KT on 06/04/16.
 */
public class StatusUtils {

    public static final String KEY_NOTIFY_STATUS_CHANGED = "key_notify_status_changed";
    public static final String STATUS_PREF = "status_pref";

    public static boolean isNotifyEnabled(Context mContext, String mobile) {
        SharedPreferences preferences = mContext.getSharedPreferences(STATUS_PREF, Context.MODE_PRIVATE);
        Set<String> stringSet = preferences.getStringSet(KEY_NOTIFY_STATUS_CHANGED, null);
        return stringSet != null && stringSet.contains(mobile);
    }

    public static void addNotifyStatus(Context mContext, String mobile) {
        SharedPreferences preferences = mContext.getSharedPreferences(STATUS_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Set<String> stringSet = preferences.getStringSet(KEY_NOTIFY_STATUS_CHANGED, null);
        if (stringSet != null && stringSet.contains(mobile)) {
            return;
        }
        Set<String> tempSet = new LinkedHashSet<>();
        if (stringSet != null) {
            tempSet.addAll(stringSet);
        }
        tempSet.add(mobile);
        editor.putStringSet(KEY_NOTIFY_STATUS_CHANGED, tempSet);
        editor.apply();
    }

    public static void removeNotifyStatus(Context mContext, String mobile) {
        SharedPreferences preferences = mContext.getSharedPreferences(STATUS_PREF, Context.MODE_PRIVATE);
        Set<String> stringSet = preferences.getStringSet(KEY_NOTIFY_STATUS_CHANGED, null);
        if (stringSet != null) {
            Set<String> tempSet = new LinkedHashSet<>();
            tempSet.addAll(stringSet);
            tempSet.remove(mobile);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putStringSet(KEY_NOTIFY_STATUS_CHANGED, tempSet);
            editor.apply();
        }
    }

    public static int getCustomCallIcon(String message, Context mContext) {
        if (mContext.getString(R.string.emergency_call).equals(message)) {
            return R.drawable.ic_call_emergency;
        } else if (mContext.getString(R.string.business_call).equals(message)) {
            return R.drawable.ic_call_business;
        } else if (mContext.getString(R.string.normal_call).equals(message)) {
            return R.drawable.ic_dial_call;
        } else {
            return R.drawable.ic_call_custom;
        }
    }

    public static int getCustomCallLayoutColor(String message, Context mContext) {
        if (mContext.getString(R.string.emergency_call).equals(message)) {
            return android.R.color.holo_red_dark;
        } else if (mContext.getString(R.string.business_call).equals(message)) {
            return android.R.color.holo_blue_dark;
        } else if (mContext.getString(R.string.casual_call).equals(message)) {
            return android.R.color.holo_purple;
        } else {
            return R.color.accentColor;
        }
    }
}
