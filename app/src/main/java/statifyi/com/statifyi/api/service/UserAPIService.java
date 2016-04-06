package statifyi.com.statifyi.api.service;

import retrofit.Response;
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
public interface UserAPIService {

    Observable<Response<StatusResponse>> getUserStatus(String mobile);

    Observable<Response<Void>> setUserStatus(StatusRequest request);

    Observable<Response<Void>> registerUser(RegisterUserRequest request);

    Observable<Response<StatusResponse>> activateUser(ActivateUserRequest request);

    Observable<Response<Boolean>> customCall(CustomCallRequest request);

    Observable<Response<Void>> registerGCM(GCMRequest request);
}
