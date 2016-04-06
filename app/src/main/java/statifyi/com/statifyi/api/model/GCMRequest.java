package statifyi.com.statifyi.api.model;

import com.google.gson.annotations.SerializedName;

import statifyi.com.statifyi.utils.NetworkUtils;

/**
 * Created by KT on 06/04/16.
 */
public class GCMRequest {

    @SerializedName("mobile")
    private String mobile;

    @SerializedName("gcmId")
    private String gcmId;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getGcmId() {
        return gcmId;
    }

    public void setGcmId(String gcmId) {
        this.gcmId = gcmId;
    }

    @Override
    public String toString() {
        return NetworkUtils.provideGson().toJson(this);
    }
}
