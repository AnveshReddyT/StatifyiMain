package statifyi.com.statifyi.api.service;

import retrofit.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import statifyi.com.statifyi.api.RemoteServerAPI;
import statifyi.com.statifyi.api.model.ActivateUserRequest;
import statifyi.com.statifyi.api.model.CustomCallRequest;
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
    public Observable<Response<StatusResponse>> getUserStatus(final String mobile) {
        return remoteServerAPI.getUserStatus(mobile)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Response<Void>> setUserStatus(StatusRequest request) {
        return remoteServerAPI.setUserStatus(request)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Response<Void>> registerUser(RegisterUserRequest request) {
        return remoteServerAPI.registerUser(request)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Response<StatusResponse>> activateUser(final ActivateUserRequest request) {
        return remoteServerAPI.activateUser(request)
                .flatMap(new Func1<Response<Void>, Observable<Response<StatusResponse>>>() {
                    @Override
                    public Observable<Response<StatusResponse>> call(Response<Void> voidResponse) {
                        if (voidResponse.code() == 200) {
                            return remoteServerAPI.getUserStatus(request.getMobile());
                        } else {
                            return Observable.just(null);
                        }
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Response<Void>> customCall(CustomCallRequest request) {
        return remoteServerAPI.customCall(request)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
