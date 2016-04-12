package statifyi.com.statifyi.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import statifyi.com.statifyi.HomeActivity;
import statifyi.com.statifyi.R;
import statifyi.com.statifyi.api.model.CustomCall;
import statifyi.com.statifyi.api.model.StatusResponse;
import statifyi.com.statifyi.data.DBHelper;
import statifyi.com.statifyi.utils.NetworkUtils;
import statifyi.com.statifyi.utils.StatusUtils;
import statifyi.com.statifyi.utils.Utils;

public class GCMIntentService extends GcmListenerService {

    public static final int NOTIFICATION_ID = 1;
    public static final String BROADCAST_ACTION_STATUS_CHANGE = "statifyi.broadcast.status_change";
    private static final String TOPICS = "/topics/";
    DBHelper dbHelper;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        if (dbHelper == null) {
            dbHelper = new DBHelper(this);
        }
        String message = data.getString("message");
        if (from.startsWith(TOPICS)) {
            try {
                StatusResponse s = NetworkUtils.provideGson().fromJson(message, StatusResponse.class);
                String status = s.getStatus().toUpperCase();
                String icon = s.getIcon();
                long time = s.getUpdatedTime().getTime();
                if (!status.isEmpty()) {
                    String phoneNumber = from.replace(TOPICS, "");
                    Utils.saveUserStatusToLocal(status, icon, phoneNumber, time, dbHelper);
                    Intent intent = new Intent(BROADCAST_ACTION_STATUS_CHANGE);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                    if (StatusUtils.isNotifyEnabled(this, phoneNumber)) {
                        sendNotification(Utils.getContactName(this, phoneNumber) + " updated his/her status");
                        StatusUtils.removeNotifyStatus(this, phoneNumber);
                    }
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        } else {
            try {
                JSONObject jsonObject = new JSONObject(message);
                CustomCall call = new CustomCall();
                call.setMobile(jsonObject.getString("from"));
                call.setMessage(jsonObject.getString("message"));
                call.setTime(System.currentTimeMillis());
                Log.d("STAT", call.toString());
                dbHelper.insertOrUpdateCustomCall(call);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // normal downstream message.
        }
    }

    private void sendNotification(String msg) {
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, HomeActivity.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_status)
                .setContentTitle("Status Changed")
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}