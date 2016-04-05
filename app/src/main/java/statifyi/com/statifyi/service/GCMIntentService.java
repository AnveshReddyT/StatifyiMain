package statifyi.com.statifyi.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import statifyi.com.statifyi.HomeActivity;
import statifyi.com.statifyi.R;
import statifyi.com.statifyi.api.model.StatusResponse;
import statifyi.com.statifyi.data.DBHelper;
import statifyi.com.statifyi.utils.Utils;

public class GCMIntentService extends GcmListenerService {

    public static final int NOTIFICATION_ID = 1;
    private static final String TOPICS = "/topics/";

    DBHelper dbHelper;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        if (dbHelper == null) {
            dbHelper = new DBHelper(this);
        }
        if (from.startsWith(TOPICS)) {
            String message = data.getString("message");
            try {
                StatusResponse s = new Gson().fromJson(message, StatusResponse.class);
                String status = s.getStatus().toUpperCase();
                String icon = s.getIcon();
                if (!status.isEmpty()) {
                    String phoneNumber = from.replace(TOPICS, "");
                    Utils.saveUserStatusToLocal(status, icon, phoneNumber, dbHelper);
//                    sendNotification(phoneNumber + " updated his/her status");
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        } else {
            // normal downstream message.
        }
    }

    private void sendNotification(String msg) {
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, HomeActivity.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("GCM Notification")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}