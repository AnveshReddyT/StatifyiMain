package statifyi.com.statifyi.api;

import com.squareup.okhttp.ResponseBody;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import statifyi.com.statifyi.api.model.TopicMessageRequest;

/**
 * Created by KT on 23/12/15.
 */
public interface FCMServerAPI {

    String BASE_CONTEXT = "/iid/info";

    String HTTP_CONTEXT = "/gcm/send";

    @Headers("Authorization:key=" + "AIzaSyDJtCaZyuwh8nknfXos1sJQk1_TJcRc5YA")
    @GET(BASE_CONTEXT + "/{token}")
    Call<ResponseBody> getFcmInfo(@Path("token") String token, @Query("details") boolean details);

    @Headers("Authorization:key=" + "AIzaSyDJtCaZyuwh8nknfXos1sJQk1_TJcRc5YA")
    @POST(HTTP_CONTEXT)
    Call<ResponseBody> sendFcmMessageToTopic(@Body TopicMessageRequest request);
}
