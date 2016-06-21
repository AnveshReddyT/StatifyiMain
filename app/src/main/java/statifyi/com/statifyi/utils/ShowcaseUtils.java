package statifyi.com.statifyi.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by apple on 6/18/16.
 */
public class ShowcaseUtils {

    public static final String KEY_HOME_PAGE = "key_home_page";
    public static final String KEY_STATUS_PAGE = "key_status_page";
    public static final String KEY_DIAERL_PAGE = "key_dialer_page";
    public static final String SHOWCASEVIEW_PREF = "showcaseview_pref";

    public static void setHomePage(Context mContext, boolean set) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHOWCASEVIEW_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_HOME_PAGE, set);
        editor.apply();
    }

    public static boolean getHomePage(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHOWCASEVIEW_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_HOME_PAGE, false);
    }

    public static void setStatusPage(Context mContext, boolean set) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHOWCASEVIEW_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_STATUS_PAGE, set);
        editor.apply();
    }

    public static boolean getStatusPage(Context mContext) {
        if(mContext != null) {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHOWCASEVIEW_PREF, Context.MODE_PRIVATE);
            return sharedPreferences.getBoolean(KEY_STATUS_PAGE, false);
        } else {
            return true;
        }
    }

    public static void setDialerPage(Context mContext, boolean set) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHOWCASEVIEW_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_DIAERL_PAGE, set);
        editor.apply();
    }

    public static boolean getDialerPage(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHOWCASEVIEW_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_DIAERL_PAGE, false);
    }

    public static void clearAll(Context mContext) {
        setHomePage(mContext, false);
        setStatusPage(mContext, false);
        setDialerPage(mContext, false);

    }
}
