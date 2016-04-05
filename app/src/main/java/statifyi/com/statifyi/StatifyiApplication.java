package statifyi.com.statifyi;

import android.app.Application;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

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

    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
    }
}
