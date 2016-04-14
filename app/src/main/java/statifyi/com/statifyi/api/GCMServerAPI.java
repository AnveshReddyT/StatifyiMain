package statifyi.com.statifyi.api;

import com.squareup.okhttp.ResponseBody;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by KT on 23/12/15.
 */
public interface GCMServerAPI {

    String BASE_CONTEXT = "/iid/info";

    @Headers("Authorization:key=" + "AIzaSyD8COPh617MLAv78-5uB_kOn7Ll8NTswKo")
    @GET(BASE_CONTEXT + "/{token}")
    Call<ResponseBody> getGcmInfo(@Path("token") String token, @Query("details") boolean details);
}
