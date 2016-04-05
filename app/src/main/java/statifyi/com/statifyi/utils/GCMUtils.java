package statifyi.com.statifyi.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by KT on 02/04/16.
 */
public class GCMUtils {

    private static final String GCM_PREF = "gcm_pref";
    private static final String REG_ID = "regId";
    private static final String GCM_SEND_SERVER = "gcm_send_server";
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
}
