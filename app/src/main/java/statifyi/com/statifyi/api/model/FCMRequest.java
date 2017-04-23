package statifyi.com.statifyi.api.model;

import com.google.gson.annotations.SerializedName;

import statifyi.com.statifyi.utils.NetworkUtils;

/**
 * Created by KT on 06/04/16.
 */
public class FCMRequest {

    @SerializedName("mobile")
    private String mobile;

    @SerializedName("gcmId")
    private String fcmId;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getFcmId() {
        return fcmId;
    }

    public void setFcmId(String fcmId) {
        this.fcmId = fcmId;
    }

    @Override
    public String toString() {
        return NetworkUtils.provideGson().toJson(this);
    }
}
