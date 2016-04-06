package statifyi.com.statifyi.api;

import retrofit.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Query;
import rx.Observable;
import statifyi.com.statifyi.api.model.ActivateUserRequest;
import statifyi.com.statifyi.api.model.CustomCallRequest;
import statifyi.com.statifyi.api.model.GCMRequest;
import statifyi.com.statifyi.api.model.RegisterUserRequest;
import statifyi.com.statifyi.api.model.StatusRequest;
import statifyi.com.statifyi.api.model.StatusResponse;

/**
 * Created by KT on 23/12/15.
 */
public interface RemoteServerAPI {

    String BASE_CONTEXT = "/Statifyi/src/users";

    @GET(BASE_CONTEXT + "/status")
    Observable<Response<StatusResponse>> getUserStatus(@Query("mobile") String mobile);

    @PUT(BASE_CONTEXT + "/status")
    Observable<Response<Void>> setUserStatus(@Body StatusRequest request);

    @POST(BASE_CONTEXT + "/register")
    Observable<Response<Void>> registerUser(@Body RegisterUserRequest request);

    @POST(BASE_CONTEXT + "/activate")
    Observable<Response<Void>> activateUser(@Body ActivateUserRequest request);

    @POST(BASE_CONTEXT + "/custom")
    Observable<Response<Boolean>> customCall(@Body CustomCallRequest request);

    @POST(BASE_CONTEXT + "/gcmId")
    Observable<Response<Void>> registerGCM(@Body GCMRequest request);
}
