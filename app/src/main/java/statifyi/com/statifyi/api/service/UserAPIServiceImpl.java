package statifyi.com.statifyi.api.service;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.util.List;

import retrofit.Call;
import statifyi.com.statifyi.api.RemoteServerAPI;
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
public class UserAPIServiceImpl implements UserAPIService {

    private final RemoteServerAPI remoteServerAPI;

    public UserAPIServiceImpl(RemoteServerAPI remoteServerAPI) {
        this.remoteServerAPI = remoteServerAPI;
    }

    @Override
    public Call<StatusResponse> getUserStatus(String mobile) {
        return remoteServerAPI.getUserStatus(mobile);
    }

    @Override
    public Call<Void> setUserStatus(String gcmId, StatusRequest request) {
        return remoteServerAPI.setUserStatus(gcmId, request);
    }

    @Override
    public Call<Void> setUserName(String gcmId, UserNameRequest request) {
        return remoteServerAPI.setUserName(gcmId, request);
    }

    @Override
    public Call<Void> uploadImage(String gcmId, File file) {
        RequestBody photo = RequestBody.create(MediaType.parse("image/*"), file);
        RequestBody body = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart("file", file.getName(), photo)
                .build();
        return remoteServerAPI.uploadImage(gcmId, photo);
    }

    @Override
    public Call<List<MultiStatusResponse>> getAllStatus(List<String> mobiles) {
        return remoteServerAPI.getAllStatus(mobiles);
    }

    @Override
    public Call<Void> registerUser(RegisterUserRequest request) {
        return remoteServerAPI.registerUser(request);
    }

    @Override
    public Call<Void> resendOtp(RegisterUserRequest request) {
        return remoteServerAPI.resendOtp(request);
    }

    @Override
    public Call<Void> activateUser(final ActivateUserRequest request) {
        return remoteServerAPI.activateUser(request);
    }

    @Override
    public Call<Boolean> customCall(String gcmId, CustomCallRequest request) {
        return remoteServerAPI.customCall(gcmId, request);
    }

    @Override
    public Call<Void> registerGCM(GCMRequest request) {
        return remoteServerAPI.registerGCM(request);
    }

}
