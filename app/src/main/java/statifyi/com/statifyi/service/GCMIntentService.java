package statifyi.com.statifyi.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import statifyi.com.statifyi.HomeActivity;
import statifyi.com.statifyi.R;
import statifyi.com.statifyi.StatifyiApplication;
import statifyi.com.statifyi.api.model.CustomCall;
import statifyi.com.statifyi.api.model.StatusResponse;
import statifyi.com.statifyi.data.DBHelper;
import statifyi.com.statifyi.utils.NetworkUtils;
import statifyi.com.statifyi.utils.StatusUtils;
import statifyi.com.statifyi.utils.Utils;

public class GCMIntentService extends GcmListenerService {

    public static final int NOTIFICATION_ID = 1;
    public static final String BROADCAST_ACTION_STATUS_CHANGE = "statifyi.broadcast.status_change";
    public static final String TOPICS = "/topics/";
    private DBHelper dbHelper;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        if (dbHelper == null) {
            dbHelper = DBHelper.getInstance(this);
        }
         String message = data.getString("message");
        if (from.startsWith(TOPICS)) {
            try {
                JSONObject jsonObject = new JSONObject(message);
                if (jsonObject.has("logout")) {
                    StatifyiApplication.logout(GCMIntentService.this);
                } else if (jsonObject.has("customCall")) {
                    parseCustomCallMessage(jsonObject.getJSONObject("value"));
                } else if (jsonObject.has("mobile")) {
                    parseStatusMessage(jsonObject.getString("mobile"), jsonObject.getString("value"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Crashlytics.logException(e.fillInStackTrace());
            }
        } else {
            // normal downstream message.
            try {
                JSONObject jsonObject = new JSONObject(message);
                if (jsonObject.has("logout")) {
                    StatifyiApplication.logout(GCMIntentService.this);
                } else if (jsonObject.has("customCall")) {
                    parseCustomCallMessage(jsonObject.getJSONObject("value"));
                }  else if (jsonObject.has("mobile")) {
                    parseStatusMessage(jsonObject.getString("mobile"), jsonObject.getString("value"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Crashlytics.logException(e.fillInStackTrace());
            }
        }
    }

    private void parseCustomCallMessage(JSONObject jsonObject) throws JSONException {
        CustomCall call = new CustomCall();
        call.setMobile(jsonObject.getString("mobile"));
        call.setMessage(jsonObject.getString("message"));
        call.setTime(System.currentTimeMillis());
        dbHelper.insertOrUpdateCustomCall(call);
    }

    private void parseStatusMessage(String phoneNumber, String message) {
        StatusResponse s = NetworkUtils.provideGson().fromJson(message, StatusResponse.class);
        String status = s.getStatus().toUpperCase();
        String name = s.getName();
        String icon = s.getIcon();
        int autoStatus = s.getAutoStatus();
        long time = s.getUpdatedTime();
        if (!status.isEmpty()) {
            Utils.saveUserStatusToLocal(status, name, icon, phoneNumber, autoStatus, time, dbHelper);
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(BROADCAST_ACTION_STATUS_CHANGE));
            if (StatusUtils.isNotifyEnabled(this, phoneNumber)) {
                sendNotification(Utils.getContactName(this, phoneNumber) + " updated his/her status to " + status);
                StatusUtils.removeNotifyStatus(this, phoneNumber);
            }
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