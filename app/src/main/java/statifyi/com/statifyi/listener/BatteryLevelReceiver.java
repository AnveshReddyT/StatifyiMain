package statifyi.com.statifyi.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import statifyi.com.statifyi.R;
import statifyi.com.statifyi.api.model.StatusRequest;
import statifyi.com.statifyi.api.service.UserAPIService;
import statifyi.com.statifyi.utils.DataUtils;
import statifyi.com.statifyi.utils.NetworkUtils;

public class BatteryLevelReceiver extends BroadcastReceiver {

    private DataUtils dataUtils;

    private UserAPIService userAPIService;

    private SharedPreferences sharedPreferences;

    public BatteryLevelReceiver() {
    }

    @Override
    public void onReceive(final Context context, Intent intent) {

        if (dataUtils == null || userAPIService == null || sharedPreferences == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            dataUtils = new DataUtils(sharedPreferences);
            userAPIService = NetworkUtils.provideUserAPIService(context);
        }

        if (!sharedPreferences.getBoolean(context.getString(R.string.key_auto_status), false)) {
            return;
        }
        if (sharedPreferences.getBoolean(context.getResources().getString(R.string.key_low_battery), false)) {
            String action = intent.getAction();
            if (Intent.ACTION_BATTERY_LOW.equals(action)) {
                String status = "On Low Battery";
                updateStatus(status);
                dataUtils.saveAutoStatus(status);
                dataUtils.saveAutoStatusIcon(R.drawable.on_low_battery);
            } else if (Intent.ACTION_BATTERY_OKAY.equals(action)) {
                dataUtils.saveAutoStatus(null);
                dataUtils.saveAutoStatusIcon(0);
                updateStatus(dataUtils.getStatus());
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
