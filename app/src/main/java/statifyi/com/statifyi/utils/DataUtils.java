package statifyi.com.statifyi.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.ImageView;

import java.io.File;

import statifyi.com.statifyi.R;

/**
 * Created by KT on 28/12/15.
 */
public class DataUtils {

    public static final String KEY_MOBILE = "key_mobile";
    public static final String KEY_COUNTRY_CODE = "key_country_code";
    public static final String KEY_NAME = "key_name";
    public static final String KEY_STATUS = "key_status";
    public static final String KEY_AUTO_STATUS = "key_auto_status";
    public static final String KEY_ICON = "key_icon";
    public static final String KEY_AUTO_STATUS_ICON = "key_auto_status_icon";
    public static final String KEY_ACTIVE = "key_active";
    public static final String KEY_AVATAR_CHANGED = "key_avatar_changed";
    private static final String USER_PREF = "user_pref";

    public static void saveMobile(Context mContext, String mobile) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_MOBILE, mobile);
        editor.apply();
    }

    public static String getCountryCode(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_COUNTRY_CODE, null);
    }

    public static void saveCountryCode(Context mContext, String countryCode) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_COUNTRY_CODE, countryCode);
        editor.apply();
    }

    public static String getMobileNumber(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_MOBILE, null);
    }

    public static void saveName(Context mContext, String name) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_NAME, name);
        editor.apply();
    }

    public static String getName(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_NAME, null);
    }

    public static void saveStatus(Context mContext, String status) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_AUTO_STATUS, null);
        editor.putString(KEY_STATUS, status);
        editor.apply();
    }

    public static void saveAutoStatus(Context mContext, String status) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_AUTO_STATUS, status);
        editor.apply();
    }

    public static String getStatus(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_STATUS, null);
    }

    public static String getAutoStatus(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_AUTO_STATUS, null);
    }

    public static void saveIcon(Context mContext, int icon) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_ICON, icon);
        editor.apply();
    }

    public static int getStatusIcon(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_ICON, R.drawable.ic_launcher);
    }

    public static void saveAutoStatusIcon(Context mContext, int icon) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_AUTO_STATUS_ICON, icon);
        editor.apply();
    }

    public static int getAutoStatusIcon(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_AUTO_STATUS_ICON, R.drawable.ic_launcher);
    }

    public static void setActive(Context mContext, boolean active) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_ACTIVE, active);
        editor.apply();
    }

    public static boolean isActivated(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_ACTIVE, false);
    }

    public static void setAvatarChanged(Context mContext, boolean active) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_AVATAR_CHANGED, active);
        editor.apply();
    }

    public static boolean isAvatarChanged(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_AVATAR_CHANGED, false);
    }

    public static void setUserImage(Context mContext, ImageView imageView) {
        File croppedImageFile = new File(mContext.getFilesDir(), "user.jpg");
        if (croppedImageFile.exists() && croppedImageFile.length() > 0) {
            imageView.setImageBitmap(null);
            imageView.setImageURI(Uri.fromFile(croppedImageFile));
        } else {
            imageView.setImageResource(R.drawable.avatar);
        }
    }

    public static boolean copyCroppedImage(Context mContext) {
        File croppedImageFile = new File(mContext.getFilesDir(), "cropped.jpg");
        File userImageFile = new File(mContext.getFilesDir(), "user.jpg");
        userImageFile.delete();
        boolean renamed = croppedImageFile.renameTo(userImageFile);
        if (renamed) {
            setAvatarChanged(mContext, true);
        }
        return renamed;
    }

    public static boolean userImageExists(Context mContext) {
        File userImageFile = new File(mContext.getFilesDir(), "user.jpg");
        return userImageFile.exists();
    }
}
