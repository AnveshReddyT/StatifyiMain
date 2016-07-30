package statifyi.com.statifyi.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import statifyi.com.statifyi.R;
import statifyi.com.statifyi.StatifyiApplication;
import statifyi.com.statifyi.api.model.StatusRequest;
import statifyi.com.statifyi.api.service.UserAPIService;
import statifyi.com.statifyi.fragment.StatusFragment;
import statifyi.com.statifyi.utils.DataUtils;
import statifyi.com.statifyi.utils.GCMUtils;
import statifyi.com.statifyi.utils.NetworkUtils;
import statifyi.com.statifyi.utils.Utils;

public class ActivityRecognizeService extends IntentService {

    private static final String IN_DRIVING = "Driving";

    private int stillCount = 0;

    private SharedPreferences sharedPreferences;

    public ActivityRecognizeService() {
        super("ActivityRecognizeService");
    }

    public ActivityRecognizeService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivities(result.getProbableActivities());
        }
    }

    private void handleDetectedActivities(List<DetectedActivity> probableActivities) {
        for (DetectedActivity activity : probableActivities) {
            switch (activity.getType()) {
                case DetectedActivity.IN_VEHICLE: {
                    Log.e("STAT", "In Vehicle: " + activity.getConfidence());
                    if (activity.getConfidence() >= 90) {
                        showNotification("Are you driving?");
                        changeStatus("DRIVING");
                    }
                    break;
                }
                case DetectedActivity.ON_BICYCLE: {
                    Log.e("STAT", "On Bicycle: " + activity.getConfidence());
                    if (activity.getConfidence() >= 90) {
                        showNotification("Are you driving?");
                        changeStatus("DRIVING");
                    }
                    break;
                }
                case DetectedActivity.ON_FOOT: {
                    Log.e("STAT", "On Foot: " + activity.getConfidence());
                    break;
                }
                case DetectedActivity.RUNNING: {
                    Log.e("STAT", "Running: " + activity.getConfidence());
                    break;
                }
                case DetectedActivity.STILL: {
                    Log.e("STAT", "Still: " + activity.getConfidence());
                    if (activity.getConfidence() >= 90) {
                        changeStatus("STILL");
                    }
                    break;
                }
                case DetectedActivity.TILTING: {
                    Log.e("STAT", "Tilting: " + activity.getConfidence());
                    break;
                }
                case DetectedActivity.WALKING: {
                    Log.e("STAT", "Walking: " + activity.getConfidence());
                    break;
                }
                case DetectedActivity.UNKNOWN: {
                    Log.e("STAT", "Unknown: " + activity.getConfidence());
                    break;
                }
            }
        }
    }

    private void changeStatus(String activity) {
        if (sharedPreferences == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        }
        if (!sharedPreferences.getBoolean(getString(R.string.key_auto_status), false)) {
            return;
        }
        if (sharedPreferences.getBoolean(getResources().getString(R.string.key_driving_mode), false)) {
            String autoStatus = DataUtils.getAutoStatus(this);
            if ("DRIVING".equals(activity)) {
                String status = IN_DRIVING;
                if (!status.equals(autoStatus)) {
                    stillCount = 0;
                    updateStatus(this, status, true);
                }
            } else if ("STILL".equals(activity)) {
                stillCount++;
                if (autoStatus != null && IN_DRIVING.equals(autoStatus) && stillCount == 3) {
                    stillCount = 0;
                    updateStatus(this, DataUtils.getStatus(this), false);
                }
            }
        }
    }

    private void showNotification(String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentText(message);
        builder.setSmallIcon(R.drawable.ic_status);
        builder.setContentTitle(getString(R.string.app_name));
        builder.setVibrate(new long[]{1000, 1000, 1000});
        NotificationManagerCompat.from(this).notify(0, builder.build());
    }

    private void updateStatus(final Context context, final String status, final boolean autoStatus) {
        UserAPIService userAPIService = NetworkUtils.provideUserAPIService(context);
        StatusRequest request = new StatusRequest();
        request.setStatus(status);
        request.setIcon(status);
        request.setAutoStatus(1);
        userAPIService.setUserStatus(GCMUtils.getRegistrationId(context), request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Response<Void> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    if (autoStatus) {
                        DataUtils.saveAutoStatus(ActivityRecognizeService.this, status);
                        DataUtils.saveAutoStatusIcon(ActivityRecognizeService.this, Utils.getDrawableResByName(ActivityRecognizeService.this, status));
                    } else {
                        DataUtils.saveAutoStatus(ActivityRecognizeService.this, null);
                        DataUtils.saveAutoStatusIcon(ActivityRecognizeService.this, 0);
                    }
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(StatusFragment.BROADCAST_ACTION_STATUS_UPDATE));
                } else if (response.code() == 401) {
                    StatifyiApplication.logout(context);
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }
}
