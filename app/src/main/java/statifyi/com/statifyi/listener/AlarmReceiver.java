package statifyi.com.statifyi.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import statifyi.com.statifyi.utils.Utils;

public class AlarmReceiver extends BroadcastReceiver {

    public static final int TIMELY_STATUS_ALARM_START_ID = 990;
    public static final int TIMELY_STATUS_ALARM_END_ID = 991;

    private UserAPIService userAPIService;

    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        userAPIService = NetworkUtils.provideUserAPIService(context);

        if (!sharedPreferences.getBoolean(context.getString(R.string.key_auto_status), false)) {
            return;
        }

        if (extras != null && extras.containsKey("alarm_id")) {
            int reqCode = extras.getInt("alarm_id");
            String status = extras.getString("timely_status");
            if (TIMELY_STATUS_ALARM_START_ID == reqCode) {
                DataUtils.saveAutoStatus(context, status);
                DataUtils.saveAutoStatusIcon(context, Utils.getDrawableResByName(context, status));
                updateStatus(context, status);
            } else if (TIMELY_STATUS_ALARM_END_ID == reqCode) {
                String autoStatus = DataUtils.getAutoStatus(context);
                if (autoStatus != null) {
                    DataUtils.saveAutoStatus(context, null);
                    DataUtils.saveAutoStatusIcon(context, 0);
                    updateStatus(context, DataUtils.getStatus(context));
                }
            }
        }
    }

    private void updateStatus(final Context context, String status) {
        String mStatus = DataUtils.getStatus(context);
        if (!mStatus.equals(status)) {
            StatusRequest request = new StatusRequest();
            request.setStatus(status);
            request.setIcon(status);
            request.setAutoStatus(2);
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
