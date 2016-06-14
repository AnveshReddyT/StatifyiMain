package statifyi.com.statifyi;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.squareup.picasso.Picasso;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

import java.io.File;

import statifyi.com.statifyi.model.TimelyStatus;
import statifyi.com.statifyi.utils.DataUtils;
import statifyi.com.statifyi.utils.GCMUtils;
import statifyi.com.statifyi.utils.NetworkUtils;
import statifyi.com.statifyi.utils.StatusUtils;
import statifyi.com.statifyi.utils.TimelyStatusUtils;

/**
 * Created by KT on 23/12/15.
 */

@ReportsCrashes(
        httpMethod = HttpSender.Method.PUT,
        reportType = HttpSender.Type.JSON,
        formUri = "http://54.201.38.232:5984/acra-statifyi/_design/acra-storage/_update/report",
        formUriBasicAuthLogin = "android",
        formUriBasicAuthPassword = "android"
)
public class StatifyiApplication extends Application {

    public static boolean deleteFile(File file) {
        boolean deletedAll = true;
        if (file != null) {
            if (file.isDirectory()) {
                String[] children = file.list();
                for (String aChildren : children) {
                    deletedAll = deleteFile(new File(file, aChildren)) && deletedAll;
                }
            } else {
                deletedAll = file.delete();
            }
        }
        return deletedAll;
    }

    public static void clearSharedPreferences(Context mContext, String file) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(file, MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
        Picasso picasso = new Picasso.Builder(this)
                .loggingEnabled(BuildConfig.DEBUG)
                .indicatorsEnabled(BuildConfig.DEBUG)
                .downloader(NetworkUtils.createBigCacheDownloader(this)).build();
        Picasso.setSingletonInstance(picasso);
    }

    private static void clearApplicationData(Context mContext) {
        File cacheDirectory = mContext.getCacheDir();
        File applicationDirectory = new File(cacheDirectory.getParent());
        if (applicationDirectory.exists()) {
            String[] fileNames = applicationDirectory.list();
            for (String fileName : fileNames) {
                if (!fileName.equals("lib")) {
                    deleteFile(new File(applicationDirectory, fileName));
                }
            }
        }
    }

    private static void clearAllSharedPreferences(Context mContext) {
        String[] files = {DataUtils.USER_PREF, GCMUtils.GCM_PREF, StatusUtils.STATUS_PREF, TimelyStatusUtils.PREF_TIMELY_STATUS};
        for (String file : files) {
            clearSharedPreferences(mContext, file);
        }
    }

    public static void logout(Context mContext) {
        if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
            ((ActivityManager)mContext.getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData();
        } else {
            clearApplicationData(mContext);
            clearAllSharedPreferences(mContext);
        }
    }
}
