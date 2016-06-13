package statifyi.com.statifyi.api.service;

import java.io.File;
import java.util.List;

import retrofit.Call;
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
public interface UserAPIService {

    Call<StatusResponse> getUserStatus(String gcmId);

    Call<Void> setUserStatus(String gcmId, StatusRequest request);

    Call<Void> setUserName(String gcmId, UserNameRequest request);

    Call<Void> uploadImage(String gcmId, File file);

    Call<List<MultiStatusResponse>> getAllStatus(List<String> mobiles);

    Call<Void> registerUser(RegisterUserRequest request);

    Call<Void> resendOtp(RegisterUserRequest request);

    Call<Void> activateUser(ActivateUserRequest request);

    Call<Boolean> customCall(String gcmId, CustomCallRequest request);

    Call<Void> registerGCM(GCMRequest request);
}
