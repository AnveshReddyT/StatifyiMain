package statifyi.com.statifyi.api.service;

import com.squareup.okhttp.ResponseBody;

import retrofit.Call;
import statifyi.com.statifyi.api.model.TopicMessageRequest;

/**
 * Created by KT on 13/04/16.
 */
public interface FCMAPIService {

    Call<ResponseBody> getFcmInfo(String token);

    Call<ResponseBody> sendFcmMessaheToTopic(TopicMessageRequest request);
}
