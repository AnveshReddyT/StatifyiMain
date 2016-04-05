package statifyi.com.statifyi.listener;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import statifyi.com.statifyi.service.FloatingService;

public class OnBootReceiver extends BroadcastReceiver {

    private AlarmManager alarmManager;

    public OnBootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent serviceIntent = new Intent(context, FloatingService.class);
        context.startService(serviceIntent);
    }
}
