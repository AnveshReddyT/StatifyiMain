package statifyi.com.statifyi.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import statifyi.com.statifyi.model.TimelyStatus;

/**
 * Created by KT on 15/03/16.
 */
public class TimelyStatusUtils {

    private static final String PREF_TIMELY_STATUS = "pref_timely_status";

    public static void saveTimelyStatusList(Context mContext, TimelyStatus[] statusList) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(PREF_TIMELY_STATUS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("key_timely_status_list", new Gson().toJson(statusList));
        editor.apply();
    }

    public static void saveTimelyStatus(Context mContext, TimelyStatus status) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(PREF_TIMELY_STATUS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("key_timely_status", status.toString());
        editor.apply();
    }

    public static TimelyStatus[] getTimelyStatusList(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(PREF_TIMELY_STATUS, Context.MODE_PRIVATE);
        String statusList = sharedPreferences.getString("key_timely_status_list", null);
        if (statusList != null) {
            return new Gson().fromJson(statusList, TimelyStatus[].class);
        } else {
            return null;
        }
    }

    public static TimelyStatus getTimelyStatus(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(PREF_TIMELY_STATUS, Context.MODE_PRIVATE);
        String status = sharedPreferences.getString("key_timely_status", null);
        if (status != null) {
            return new Gson().fromJson(status, TimelyStatus.class);
        } else {
            return null;
        }
    }
}
