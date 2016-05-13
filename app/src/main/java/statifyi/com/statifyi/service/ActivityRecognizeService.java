package statifyi.com.statifyi.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import statifyi.com.statifyi.R;
import statifyi.com.statifyi.api.model.StatusRequest;
import statifyi.com.statifyi.api.service.UserAPIService;
import statifyi.com.statifyi.utils.DataUtils;
import statifyi.com.statifyi.utils.Utils;

public class ActivityRecognizeService extends IntentService {

    private static final String IN_DRIVING = "Driving";

    private UserAPIService userAPIService;

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
                    if (activity.getConfidence() >= 75) {
                        showNotification("Are you driving?");
                        changeStatus("DRIVING");
                    }
                    break;
                }
                case DetectedActivity.ON_BICYCLE: {
                    Log.e("STAT", "On Bicycle: " + activity.getConfidence());
                    if (activity.getConfidence() >= 75) {
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
        if (!sharedPreferences.getBoolean(getString(R.string.key_auto_status), false)) {
            return;
        }
        if (sharedPreferences.getBoolean(getResources().getString(R.string.key_driving_mode), false)) {
            if ("DRIVING".equals(activity)) {
                String status = IN_DRIVING;
                DataUtils.saveAutoStatus(this, status);
                DataUtils.saveAutoStatusIcon(this, Utils.getDrawableResByName(this, status));
                updateStatus(this, status);
            } else if ("STILL".equals(activity)) {
                String autoStatus = DataUtils.getAutoStatus(this);
                if (autoStatus != null && IN_DRIVING.equals(autoStatus)) {
                    DataUtils.saveAutoStatus(this, null);
                    DataUtils.saveAutoStatusIcon(this, 0);
                    updateStatus(this, DataUtils.getStatus(this));
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

    private void updateStatus(Context context, String status) {
        StatusRequest request = new StatusRequest();
        request.setMobile(DataUtils.getMobileNumber(context));
        request.setStatus(status);
        request.setIcon(status);
        request.setAutoStatus(1);
        userAPIService.setUserStatus(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Response<Void> response, Retrofit retrofit) {

            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }
}
