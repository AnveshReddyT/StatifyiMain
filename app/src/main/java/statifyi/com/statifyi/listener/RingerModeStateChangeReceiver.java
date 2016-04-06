package statifyi.com.statifyi.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;

import retrofit.Response;
import rx.functions.Action1;
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
                        updateStatus(dataUtils.getStatus());
                    }
                }
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                if (auto_vibrate_status_enabled) {
                    status = "In Vibration Mode";
                    updateStatus(status);
                    dataUtils.saveAutoStatus(status);
                }
                break;

            case AudioManager.RINGER_MODE_SILENT:
                if (auto_silent_status_enabled) {
                    status = "In Silent Mode";
                    updateStatus(status);
                    dataUtils.saveAutoStatus(status);
                }
                break;
        }
    }

    private void updateStatus(String status) {
        StatusRequest request = new StatusRequest();
        request.setMobile(dataUtils.getMobileNumber());
        request.setStatus(status);
        request.setIcon(status);
        userAPIService.setUserStatus(request).subscribe(new Action1<Response>() {
            @Override
            public void call(Response s) {
                if (s.code() == 200) {
//                        dataUtils.saveStatus(status);
//                        int ico = Utils.getDrawableResByName(context, status);
//                        dataUtils.saveIcon(ico);
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }
}
