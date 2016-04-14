package statifyi.com.statifyi.api.service;

import com.squareup.okhttp.ResponseBody;

import retrofit.Call;

/**
 * Created by KT on 13/04/16.
 */
public interface GCMAPIService {

    Call<ResponseBody> getGcmInfo(String token);
}
