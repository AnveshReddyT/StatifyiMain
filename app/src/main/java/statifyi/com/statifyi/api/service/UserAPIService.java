package statifyi.com.statifyi.api.service;

import java.io.File;
import java.util.List;

import retrofit.Call;
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
public interface UserAPIService {

    Call<StatusResponse> getUserStatus(String fcmId, String mobile);

    Call<Void> setUserStatus(String fcmId, StatusRequest request);

    Call<Void> setUserName(String fcmId, UserNameRequest request);

    Call<Void> uploadImage(String fcmId, File file);

    Call<List<MultiStatusResponse>> getAllStatus(String fcmId, List<String> mobiles);

    Call<Void> registerUser(RegisterUserRequest request);

    Call<Void> resendOtp(RegisterUserRequest request);

    Call<Void> activateUser(ActivateUserRequest request);

    Call<Boolean> customCall(String fcmId, CustomCallRequest request);

    Call<Void> registerFCM(FCMRequest request);
}
