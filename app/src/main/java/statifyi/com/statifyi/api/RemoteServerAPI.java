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
import retrofit.http.Query;
import statifyi.com.statifyi.api.model.ActivateUserRequest;
import statifyi.com.statifyi.api.model.CustomCallRequest;
import statifyi.com.statifyi.api.model.FCMRequest;
import statifyi.com.statifyi.api.model.MultiStatusResponse;
import statifyi.com.statifyi.api.model.RegisterUserRequest;
import statifyi.com.statifyi.api.model.StatusRequest;
import statifyi.com.statifyi.api.model.StatusResponse;
import statifyi.com.statifyi.api.model.UserNameRequest;

/**
 * Created by KT on 23/12/15.
 */
public interface RemoteServerAPI {

    String BASE_CONTEXT = "/src/users";

    @GET(BASE_CONTEXT + "/status")
    Call<StatusResponse> getUserStatus(@Header("token") String fcmId, @Query("mobile") String mobile);

    @PUT(BASE_CONTEXT + "/status")
    Call<Void> setUserStatus(@Header("token") String fcmId, @Body StatusRequest request);

    @PUT(BASE_CONTEXT + "/name")
    Call<Void> setUserName(@Header("token") String fcmId, @Body UserNameRequest request);

    @Multipart
    @POST(BASE_CONTEXT + "/image")
    Call<Void> uploadImage(@Header("token") String fcmId, @Part("file") RequestBody request);

    @POST(BASE_CONTEXT + "/multiStatus")
    Call<List<MultiStatusResponse>> getAllStatus(@Header("token") String fcmId, @Body List<String> mobiles);

    @POST(BASE_CONTEXT + "/register")
    Call<Void> registerUser(@Body RegisterUserRequest request);

    @POST(BASE_CONTEXT + "/resendOTP")
    Call<Void> resendOtp(@Body RegisterUserRequest request);

    @POST(BASE_CONTEXT + "/activate")
    Call<Void> activateUser(@Body ActivateUserRequest request);

    @POST(BASE_CONTEXT + "/custom")
    Call<Boolean> customCall(@Header("token") String fcmId, @Body CustomCallRequest request);

    @POST(BASE_CONTEXT + "/gcmId")
    Call<Void> registerFCM(@Body FCMRequest request);
}
