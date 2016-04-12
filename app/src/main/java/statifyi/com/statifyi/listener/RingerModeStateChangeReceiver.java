package statifyi.com.statifyi.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import statifyi.com.statifyi.R;
import statifyi.com.statifyi.api.model.StatusRequest;
import statifyi.com.statifyi.api.service.UserAPIService;
import statifyi.com.statifyi.utils.DataUtils;
import statifyi.com.statifyi.utils.NetworkUtils;

public class RingerModeStateChangeReceiver extends BroadcastReceiver {

    private DataUtils dataUtils;

    private UserAPIService userAPIService;

    private SharedPreferences sharedPreferences;

    public RingerModeStateChangeReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (dataUtils == null || userAPIService == null || sharedPreferences == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            dataUtils = new DataUtils(sharedPreferences);
            userAPIService = NetworkUtils.provideUserAPIService(context);

        }

        if (!sharedPreferences.getBoolean(context.getString(R.string.key_auto_status), false)) {
            return;
        }
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        String status;
        boolean auto_vibrate_status_enabled = sharedPreferences.getBoolean(context.getResources().getString(R.string.key_vibrating_mode), false);
        boolean auto_silent_status_enabled = sharedPreferences.getBoolean(context.getResources().getString(R.string.key_silent_mode), false);

        switch (audio.getRingerMode()) {
            case AudioManager.RINGER_MODE_NORMAL:
                if (auto_silent_status_enabled || auto_vibrate_status_enabled) {
                    if (dataUtils.getAutoStatus() != null) {
                        dataUtils.saveAutoStatus(null);
                        dataUtils.saveAutoStatusIcon(0);
                        updateStatus(dataUtils.getStatus());
                    }
                }
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                if (auto_vibrate_status_enabled) {
                    status = "In Vibration Mode";
                    updateStatus(status);
                    dataUtils.saveAutoStatus(status);
                    dataUtils.saveAutoStatusIcon(R.drawable.in_vibration_mode);
                }
                break;

            case AudioManager.RINGER_MODE_SILENT:
                if (auto_silent_status_enabled) {
                    status = "In Silent Mode";
                    updateStatus(status);
                    dataUtils.saveAutoStatus(status);
                    dataUtils.saveAutoStatusIcon(R.drawable.in_silent_mode);
                }
                break;
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
