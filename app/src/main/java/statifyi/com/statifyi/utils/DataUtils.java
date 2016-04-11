package statifyi.com.statifyi.utils;

import android.content.SharedPreferences;

import statifyi.com.statifyi.R;

/**
 * Created by KT on 28/12/15.
 */
public class DataUtils {

    public static final String KEY_MOBILE = "key_mobile";
    public static final String KEY_STATUS = "key_status";
    public static final String KEY_AUTO_STATUS = "key_auto_status";
    public static final String KEY_ICON = "key_icon";
    public static final String KEY_AUTO_STATUS_ICON = "key_auto_status_icon";
    public static final String KEY_ACTIVE = "key_active";
    private final SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public DataUtils(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        editor = sharedPreferences.edit();
    }

    public void saveMobile(String mobile) {
        editor.putString(KEY_MOBILE, mobile);
        editor.apply();
    }

    public String getMobileNumber() {
        return sharedPreferences.getString(KEY_MOBILE, null);
    }

    public void saveStatus(String status) {
        editor.putString(KEY_AUTO_STATUS, null);
        editor.putString(KEY_STATUS, status);
        editor.apply();
    }

    public void saveAutoStatus(String status) {
        editor.putString(KEY_AUTO_STATUS, status);
        editor.apply();
    }

    public String getStatus() {
        return sharedPreferences.getString(KEY_STATUS, null);
    }

    public String getAutoStatus() {
        return sharedPreferences.getString(KEY_AUTO_STATUS, null);
    }

    public void saveIcon(int icon) {
        editor.putInt(KEY_ICON, icon);
        editor.apply();
    }

    public int getStatusIcon() {
        return sharedPreferences.getInt(KEY_ICON, R.drawable.ic_launcher);
    }

    public void saveAutoStatusIcon(int icon) {
        editor.putInt(KEY_AUTO_STATUS_ICON, icon);
        editor.apply();
    }

    public int getAutoStatusIcon() {
        return sharedPreferences.getInt(KEY_AUTO_STATUS_ICON, R.drawable.ic_launcher);
    }

    public void setActive(boolean active) {
        editor.putBoolean(KEY_ACTIVE, active);
        editor.apply();
    }

    public boolean isActivated() {
        return sharedPreferences.getBoolean(KEY_ACTIVE, false);
    }
}
