package statifyi.com.statifyi.provider;

import android.os.Bundle;

/**
 * Created by apple on 15/12/16.
 */
public interface AnalyticsProvider {

    void logEvent(String event, Bundle bundle);

    void logEvent(String screen, String category, String action, String label);
}
