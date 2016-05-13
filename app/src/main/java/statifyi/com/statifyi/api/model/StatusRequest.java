package statifyi.com.statifyi.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by KT on 27/12/15.
 */
public class StatusRequest {

    @SerializedName("mobile")
    private String mobile;

    @SerializedName("status")
    private String status;

    @SerializedName("icon")
    private String icon;

    @SerializedName("autoStatus")
    private int autoStatus;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getAutoStatus() {
        return autoStatus;
    }

    public void setAutoStatus(int autoStatus) {
        this.autoStatus = autoStatus;
    }
}
