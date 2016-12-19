package statifyi.com.statifyi;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.iid.InstanceID;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import io.fabric.sdk.android.Fabric;
import statifyi.com.statifyi.provider.AnalyticsProviderImpl;
import statifyi.com.statifyi.utils.DataUtils;
import statifyi.com.statifyi.utils.GCMUtils;
import statifyi.com.statifyi.utils.NetworkUtils;
import statifyi.com.statifyi.utils.StatusUtils;
import statifyi.com.statifyi.utils.TimelyStatusUtils;
import statifyi.com.statifyi.utils.Utils;

/**
 * Created by KT on 23/12/15.
 */

public class StatifyiApplication extends Application {

    private Tracker mTracker;

    private FirebaseAnalytics mFirebaseAnalytics;

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

    public static void logout(final Context mContext) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    InstanceID.getInstance(mContext).deleteInstanceID();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Utils.showToast(mContext, "Multiple devices detected!");
                if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                    ((ActivityManager) mContext.getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData();
                } else {
                    clearApplicationData(mContext);
                    clearAllSharedPreferences(mContext);
                }
            }
        }.execute();
    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     *
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker("UA-81704105-1");
        }
        return mTracker;
    }

    synchronized public FirebaseAnalytics getFirebaseAnalytics() {
        if (mFirebaseAnalytics == null) {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
            mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
            mFirebaseAnalytics.setMinimumSessionDuration(20000);
            mFirebaseAnalytics.setSessionTimeoutDuration(1000000);
        }
        return mFirebaseAnalytics;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Picasso picasso = new Picasso.Builder(this)
                .loggingEnabled(BuildConfig.DEBUG)
                .indicatorsEnabled(BuildConfig.DEBUG)
                .downloader(NetworkUtils.createBigCacheDownloader(this)).build();
        Picasso.setSingletonInstance(picasso);

        AnalyticsProviderImpl.build(this);
    }
}
