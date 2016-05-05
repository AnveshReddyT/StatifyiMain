package statifyi.com.statifyi.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

import statifyi.com.statifyi.R;

public class ActivityRecognizeService extends IntentService {

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
                    }
                    break;
                }
                case DetectedActivity.ON_BICYCLE: {
                    Log.e("STAT", "On Bicycle: " + activity.getConfidence());
                    if (activity.getConfidence() >= 75) {
                        showNotification("Are you driving?");
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

    private void showNotification(String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentText(message);
        builder.setSmallIcon(R.drawable.ic_status);
        builder.setContentTitle(getString(R.string.app_name));
        builder.setVibrate(new long[]{1000, 1000, 1000});
        NotificationManagerCompat.from(this).notify(0, builder.build());
    }
}
