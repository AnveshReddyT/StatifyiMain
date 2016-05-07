package statifyi.com.statifyi.listener;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import statifyi.com.statifyi.service.FloatingService;
import statifyi.com.statifyi.utils.DataUtils;

public class OnBootReceiver extends BroadcastReceiver {

    public OnBootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (!TextUtils.isEmpty(DataUtils.getMobileNumber(context))) {
            Intent serviceIntent = new Intent(context, FloatingService.class);
            context.startService(serviceIntent);
        }
    }
}
