package statifyi.com.statifyi.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.util.List;

import retrofit.Response;
import statifyi.com.statifyi.api.model.MultiStatusResponse;
import statifyi.com.statifyi.api.service.UserAPIService;
import statifyi.com.statifyi.data.DBHelper;
import statifyi.com.statifyi.utils.GCMUtils;
import statifyi.com.statifyi.utils.NetworkUtils;
import statifyi.com.statifyi.utils.Utils;

/**
 * Created by KT on 12/04/16.
 */
public class SyncAllStatusService extends IntentService {

    public SyncAllStatusService() {
        super("SyncAllStatusService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        UserAPIService userAPIService = NetworkUtils.provideUserAPIService(this);
        try {
            Response<List<MultiStatusResponse>> response = userAPIService.getAllStatus(GCMUtils.getRegistrationId(this), Utils.get10DigitPhoneNumbersFromContacts(this)).execute();
            List<MultiStatusResponse> multiStatusResponseList = response.body();
            DBHelper dbHelper = DBHelper.getInstance(SyncAllStatusService.this);
            for (MultiStatusResponse multiStatusResponse : multiStatusResponseList) {
                dbHelper.insertOrUpdateUser(multiStatusResponse.toUser());
            }
            LocalBroadcastManager.getInstance(SyncAllStatusService.this).sendBroadcast(new Intent(GCMIntentService.BROADCAST_ACTION_STATUS_CHANGE));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
