package statifyi.com.statifyi.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.LinkedHashSet;
import java.util.Set;

import statifyi.com.statifyi.R;

/**
 * Created by KT on 06/04/16.
 */
public class StatusUtils {

    public static boolean isNotifyEnabled(Context mContext, String mobile) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        Set<String> stringSet = preferences.getStringSet(mContext.getString(R.string.key_notify_when_status_changed), null);
        return stringSet != null && stringSet.contains(mobile);
    }

    public static void addNotifyStatus(Context mContext, String mobile) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = preferences.edit();
        Set<String> stringSet = preferences.getStringSet(mContext.getString(R.string.key_notify_when_status_changed), null);
        if (stringSet != null && stringSet.contains(mobile)) {
            return;
        }
        if (stringSet == null) {
            stringSet = new LinkedHashSet<>();
        }
        stringSet.add(mobile);
        editor.putStringSet(mContext.getString(R.string.key_notify_when_status_changed), stringSet);
        editor.apply();
    }

    public static void removeNotifyStatus(Context mContext, String mobile) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = preferences.edit();
        Set<String> stringSet = preferences.getStringSet(mContext.getString(R.string.key_notify_when_status_changed), null);
        if (stringSet != null) {
            stringSet.remove(mobile);
            editor.putStringSet(mContext.getString(R.string.key_notify_when_status_changed), stringSet);
            editor.apply();
        }
    }

    public static int getCustomCallIcon(String message, Context mContext) {
        if (mContext.getString(R.string.emergency_call).equals(message)) {
            return R.drawable.ic_call_emergency;
        } else if (mContext.getString(R.string.business_call).equals(message)) {
            return R.drawable.ic_call_business;
        } else if (mContext.getString(R.string.casual_call).equals(message)) {
            return R.drawable.ic_call_casual;
        } else {
            return R.drawable.ic_call_custom;
        }
    }
}
