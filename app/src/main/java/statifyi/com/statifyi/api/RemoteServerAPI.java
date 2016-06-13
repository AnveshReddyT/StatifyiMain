package statifyi.com.statifyi.api;

import com.squareup.okhttp.RequestBody;

import java.util.List;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import statifyi.com.statifyi.api.model.ActivateUserRequest;
import statifyi.com.statifyi.api.model.CustomCallRequest;
import statifyi.com.statifyi.api.model.GCMRequest;
import statifyi.com.statifyi.api.model.MultiStatusResponse;
import statifyi.com.statifyi.api.model.RegisterUserRequest;
import statifyi.com.statifyi.api.model.StatusRequest;
import statifyi.com.statifyi.api.model.StatusResponse;
import statifyi.com.statifyi.api.model.UserNameRequest;

/**
 * Created by KT on 23/12/15.
 */
public interface RemoteServerAPI {

    String BASE_CONTEXT = "/Statifyi/src/users";

    @GET(BASE_CONTEXT + "/status")
    Call<StatusResponse> getUserStatus(@Header("token") String gcmId);

    @PUT(BASE_CONTEXT + "/status")
    Call<Void> setUserStatus(@Header("token") String gcmId, @Body StatusRequest request);

    @PUT(BASE_CONTEXT + "/name")
    Call<Void> setUserName(@Header("token") String gcmId, @Body UserNameRequest request);

    @Multipart
    @POST(BASE_CONTEXT + "/image")
    Call<Void> uploadImage(@Header("token") String gcmId, @Part("file") RequestBody request);

    @POST(BASE_CONTEXT + "/multiStatus")
    Call<List<MultiStatusResponse>> getAllStatus(@Body List<String> mobiles);

    @POST(BASE_CONTEXT + "/register")
    Call<Void> registerUser(@Body RegisterUserRequest request);

    @POST(BASE_CONTEXT + "/resendOTP")
    Call<Void> resendOtp(@Body RegisterUserRequest request);

    @POST(BASE_CONTEXT + "/activate")
    Call<Void> activateUser(@Body ActivateUserRequest request);

    @POST(BASE_CONTEXT + "/custom")
    Call<Boolean> customCall(@Header("token") String gcmId, @Body CustomCallRequest request);

    @POST(BASE_CONTEXT + "/gcmId")
    Call<Void> registerGCM(@Body GCMRequest request);
}
