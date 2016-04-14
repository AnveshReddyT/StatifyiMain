package statifyi.com.statifyi.api;

import java.util.List;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Query;
import statifyi.com.statifyi.api.model.ActivateUserRequest;
import statifyi.com.statifyi.api.model.CustomCallRequest;
import statifyi.com.statifyi.api.model.GCMRequest;
import statifyi.com.statifyi.api.model.MultiStatusResponse;
import statifyi.com.statifyi.api.model.RegisterUserRequest;
import statifyi.com.statifyi.api.model.StatusRequest;
import statifyi.com.statifyi.api.model.StatusResponse;

/**
 * Created by KT on 23/12/15.
 */
public interface RemoteServerAPI {

    String BASE_CONTEXT = "/Statifyi/src/users";

    @GET(BASE_CONTEXT + "/status")
    Call<StatusResponse> getUserStatus(@Query("mobile") String mobile);

    @PUT(BASE_CONTEXT + "/status")
    Call<Void> setUserStatus(@Body StatusRequest request);

    @POST(BASE_CONTEXT + "/multiStatus")
    Call<List<MultiStatusResponse>> getAllStatus(@Body List<String> mobiles);

    @POST(BASE_CONTEXT + "/register")
    Call<Void> registerUser(@Body RegisterUserRequest request);

    @POST(BASE_CONTEXT + "/activate")
    Call<Void> activateUser(@Body ActivateUserRequest request);

    @POST(BASE_CONTEXT + "/custom")
    Call<Boolean> customCall(@Body CustomCallRequest request);

    @POST(BASE_CONTEXT + "/gcmId")
    Call<Void> registerGCM(@Body GCMRequest request);
}
