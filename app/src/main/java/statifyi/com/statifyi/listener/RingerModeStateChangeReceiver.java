package statifyi.com.statifyi.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import statifyi.com.statifyi.R;
import statifyi.com.statifyi.api.model.StatusRequest;
import statifyi.com.statifyi.api.service.UserAPIService;
import statifyi.com.statifyi.fragment.StatusFragment;
import statifyi.com.statifyi.utils.DataUtils;
import statifyi.com.statifyi.utils.GCMUtils;
import statifyi.com.statifyi.utils.NetworkUtils;

public class RingerModeStateChangeReceiver extends BroadcastReceiver {

    private static final String IN_VIBRATION_MODE = "In Vibration Mode";
    private static final String IN_SILENT_MODE = "In Silent Mode";
    private UserAPIService userAPIService;

    private SharedPreferences sharedPreferences;

    public RingerModeStateChangeReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        userAPIService = NetworkUtils.provideUserAPIService(context);

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
                    String autoStatus = DataUtils.getAutoStatus(context);
                    if (autoStatus != null && (IN_SILENT_MODE.equals(autoStatus) || IN_VIBRATION_MODE.equals(autoStatus))) {
                        DataUtils.saveAutoStatus(context, null);
                        DataUtils.saveAutoStatusIcon(context, 0);
                        updateStatus(DataUtils.getStatus(context), context);
                    }
                }
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                if (auto_vibrate_status_enabled) {
                    status = IN_VIBRATION_MODE;
                    updateStatus(status, context);
                    DataUtils.saveAutoStatus(context, status);
                    DataUtils.saveAutoStatusIcon(context, R.drawable.in_vibration_mode);
                }
                break;

            case AudioManager.RINGER_MODE_SILENT:
                if (auto_silent_status_enabled) {
                    status = IN_SILENT_MODE;
                    updateStatus(status, context);
                    DataUtils.saveAutoStatus(context, status);
                    DataUtils.saveAutoStatusIcon(context, R.drawable.in_silent_mode);
                }
                break;
        }
    }

    private void updateStatus(String status, final Context context) {
        String mStatus = DataUtils.getStatus(context);
        if (!mStatus.equals(status)) {
            StatusRequest request = new StatusRequest();
            request.setStatus(status);
            request.setIcon(status);
            request.setAutoStatus(1);
            userAPIService.setUserStatus(GCMUtils.getRegistrationId(context), request).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Response<Void> response, Retrofit retrofit) {
                    if (response.isSuccess()) {
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(StatusFragment.BROADCAST_ACTION_STATUS_UPDATE));
                    }
                }

                @Override
                public void onFailure(Throwable t) {

                }
            });
        }
    }
}
