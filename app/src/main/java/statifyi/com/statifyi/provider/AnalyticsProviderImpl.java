package statifyi.com.statifyi.provider;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import statifyi.com.statifyi.StatifyiApplication;

/**
 * Created by apple on 15/12/16.
 */
public class AnalyticsProviderImpl implements AnalyticsProvider {

    private static AnalyticsProvider analyticsProvider;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Context context;

    public AnalyticsProviderImpl(Context context) {
        this.context = context;
        if (mFirebaseAnalytics == null) {
            mFirebaseAnalytics = ((StatifyiApplication) context.getApplicationContext()).getFirebaseAnalytics();
        }
    }

    public static synchronized void build(Context context) {
        if (analyticsProvider == null) {
            analyticsProvider = new AnalyticsProviderImpl(context);
        }
    }

    public static AnalyticsProvider getInstance() {
        return analyticsProvider;
    }

    @Override
    public void logEvent(String event, Bundle bundle) {
        mFirebaseAnalytics.logEvent(event, bundle);
    }

    @Override
    public void logEvent(String screen, String category, String action, String label) {
        Bundle bundle = new Bundle();
        bundle.putString("ScreenName", screen);
        bundle.putString("EventCategory", category);
        bundle.putString("EventAction", action);
        bundle.putString("EventLabel", label);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }
}
