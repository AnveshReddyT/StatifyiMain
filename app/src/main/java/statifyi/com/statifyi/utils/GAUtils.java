package statifyi.com.statifyi.utils;

import android.content.Context;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import statifyi.com.statifyi.StatifyiApplication;

/**
 * Created by apple on 31/07/16.
 */
public class GAUtils {

    public static Tracker getTracker(Context applicationContext) {
        StatifyiApplication application = (StatifyiApplication) applicationContext;
        return application.getDefaultTracker();
    }

    public static void sendScreenView(Context mContext, String screenName) {
        Tracker mTracker = getTracker(mContext);
        mTracker.setScreenName(screenName);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public static void sendEvent(Context appContext, String screenName, String category, String action, String label) {
        Tracker mTracker = getTracker(appContext);
        mTracker.setScreenName(screenName);
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .build());
    }

}
