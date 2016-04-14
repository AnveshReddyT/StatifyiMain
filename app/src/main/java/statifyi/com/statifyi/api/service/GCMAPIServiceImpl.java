package statifyi.com.statifyi.api.service;

import com.squareup.okhttp.ResponseBody;

import retrofit.Call;
import statifyi.com.statifyi.api.GCMServerAPI;

/**
 * Created by KT on 13/04/16.
 */
public class GCMAPIServiceImpl implements GCMAPIService {

    private final GCMServerAPI gcmServerAPI;

    public GCMAPIServiceImpl(GCMServerAPI gcmServerAPI) {
        this.gcmServerAPI = gcmServerAPI;
    }

    @Override
    public Call<ResponseBody> getGcmInfo(String token) {
        return gcmServerAPI.getGcmInfo(token, true);
    }
}
