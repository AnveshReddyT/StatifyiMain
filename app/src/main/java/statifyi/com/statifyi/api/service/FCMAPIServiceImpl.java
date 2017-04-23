package statifyi.com.statifyi.api.service;

import com.squareup.okhttp.ResponseBody;

import retrofit.Call;
import statifyi.com.statifyi.api.FCMServerAPI;
import statifyi.com.statifyi.api.model.TopicMessageRequest;

/**
 * Created by KT on 13/04/16.
 */
public class FCMAPIServiceImpl implements FCMAPIService {

    private final FCMServerAPI FCMServerAPI;

    public FCMAPIServiceImpl(FCMServerAPI FCMServerAPI) {
        this.FCMServerAPI = FCMServerAPI;
    }

    @Override
    public Call<ResponseBody> getFcmInfo(String token) {
        return FCMServerAPI.getFcmInfo(token, true);
    }

    @Override
    public Call<ResponseBody> sendFcmMessaheToTopic(TopicMessageRequest request) {
        return FCMServerAPI.sendFcmMessageToTopic(request);
    }
}
