package statifyi.com.statifyi.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import statifyi.com.statifyi.R;
import statifyi.com.statifyi.api.model.StatusRequest;
import statifyi.com.statifyi.api.service.UserAPIService;
import statifyi.com.statifyi.utils.DataUtils;
import statifyi.com.statifyi.utils.NetworkUtils;
import statifyi.com.statifyi.utils.Utils;

public class AlarmReceiver extends BroadcastReceiver {

    public static final int TIMELY_STATUS_ALARM_START_ID = 990;
    public static final int TIMELY_STATUS_ALARM_END_ID = 991;

    private DataUtils dataUtils;

    private UserAPIService userAPIService;

    private SharedPreferences sharedPreferences;

    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();

        if (dataUtils == null || userAPIService == null || sharedPreferences == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            dataUtils = new DataUtils(sharedPreferences);
            userAPIService = NetworkUtils.provideUserAPIService(context);

        }

        if (!sharedPreferences.getBoolean(context.getString(R.string.key_auto_status), false)) {
            return;
        }

        if (extras != null && extras.containsKey("alarm_id")) {
            int reqCode = extras.getInt("alarm_id");
            String status = extras.getString("timely_status");
            if (TIMELY_STATUS_ALARM_START_ID == reqCode) {
                Log.d("STAT", "Start Alarm Triggered");
                updateStatus(status);
                dataUtils.saveAutoStatus(status);
                dataUtils.saveAutoStatusIcon(Utils.getDrawableResByName(context, status));
            } else if (TIMELY_STATUS_ALARM_END_ID == reqCode) {
                Log.d("STAT", "End Alarm Triggered");
                if (dataUtils.getAutoStatus() != null) {
                    dataUtils.saveAutoStatus(null);
                    dataUtils.saveAutoStatusIcon(0);
                    updateStatus(dataUtils.getStatus());
                }
            }
        }
    }

    private void updateStatus(String status) {
        StatusRequest request = new StatusRequest();
        request.setMobile(dataUtils.getMobileNumber());
        request.setStatus(status);
        request.setIcon(status);
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
