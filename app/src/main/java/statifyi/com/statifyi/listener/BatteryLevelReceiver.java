package statifyi.com.statifyi.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import statifyi.com.statifyi.R;
import statifyi.com.statifyi.StatifyiApplication;
import statifyi.com.statifyi.api.model.StatusRequest;
import statifyi.com.statifyi.api.service.UserAPIService;
import statifyi.com.statifyi.fragment.StatusFragment;
import statifyi.com.statifyi.utils.DataUtils;
import statifyi.com.statifyi.utils.FCMUtils;
import statifyi.com.statifyi.utils.NetworkUtils;
import statifyi.com.statifyi.utils.Utils;

public class BatteryLevelReceiver extends BroadcastReceiver {

    private static final String ON_LOW_BATTERY = "On Low Battery";

    private UserAPIService userAPIService;

    private SharedPreferences sharedPreferences;

    public BatteryLevelReceiver() {
    }

    @Override
    public void onReceive(final Context context, Intent intent) {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        userAPIService = NetworkUtils.provideUserAPIService(context);

        if (!sharedPreferences.getBoolean(context.getString(R.string.key_auto_status), false)) {
            return;
        }
        if (sharedPreferences.getBoolean(context.getResources().getString(R.string.key_low_battery), false)) {
            String action = intent.getAction();
            if (Intent.ACTION_BATTERY_LOW.equals(action)) {
                String status = ON_LOW_BATTERY;
                DataUtils.saveAutoStatus(context, status);
                DataUtils.saveAutoStatusIcon(context, Utils.getDrawableResByName(context, status));
                updateStatus(context, status);
            } else if (Intent.ACTION_BATTERY_OKAY.equals(action)) {
                String autoStatus = DataUtils.getAutoStatus(context);
                if (autoStatus != null && ON_LOW_BATTERY.equals(autoStatus)) {
                    DataUtils.saveAutoStatus(context, null);
                    DataUtils.saveAutoStatusIcon(context, 0);
                    updateStatus(context, DataUtils.getStatus(context));
                }
            }
        }
    }

    private void updateStatus(final Context context, String status) {
        StatusRequest request = new StatusRequest();
        request.setStatus(status);
        request.setIcon(status);
        request.setAutoStatus(1);
        userAPIService.setUserStatus(FCMUtils.getRegistrationId(context), request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Response<Void> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(StatusFragment.BROADCAST_ACTION_STATUS_UPDATE));
                } else if (response.code() == 401) {
                    StatifyiApplication.logout(context);
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }
}
