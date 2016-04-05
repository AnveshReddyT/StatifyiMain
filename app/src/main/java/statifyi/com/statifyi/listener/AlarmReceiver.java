package statifyi.com.statifyi.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    public static final int TIMELY_STATUS_ALARM_START_ID = 990;
    public static final int TIMELY_STATUS_ALARM_END_ID = 991;

    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        Log.d("STAT", "Alarm Triggered");
        if (extras != null && extras.containsKey("alarm_id")) {
            int reqCode = extras.getInt("alarm_id");
            if (TIMELY_STATUS_ALARM_START_ID == reqCode) {
                Log.d("STAT", "Start Alarm Triggered");
            } else if (TIMELY_STATUS_ALARM_END_ID == reqCode) {
                Log.d("STAT", "End Alarm Triggered");
            }
        }
    }
}
