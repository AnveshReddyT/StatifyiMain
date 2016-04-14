package statifyi.com.statifyi.api.service;

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

/**
 * Created by KT on 23/12/15.
 */
public class UserAPIServiceImpl implements UserAPIService {

    private final RemoteServerAPI remoteServerAPI;

    public UserAPIServiceImpl(RemoteServerAPI remoteServerAPI) {
        this.remoteServerAPI = remoteServerAPI;
    }

    @Override
    public Call<StatusResponse> getUserStatus(final String mobile) {
        return remoteServerAPI.getUserStatus(mobile);
    }

    @Override
    public Call<Void> setUserStatus(StatusRequest request) {
        return remoteServerAPI.setUserStatus(request);
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
    public Call<Void> activateUser(final ActivateUserRequest request) {
        return remoteServerAPI.activateUser(request);
    }

    @Override
    public Call<Boolean> customCall(CustomCallRequest request) {
        return remoteServerAPI.customCall(request);
    }

    @Override
    public Call<Void> registerGCM(GCMRequest request) {
        return remoteServerAPI.registerGCM(request);
    }

}
