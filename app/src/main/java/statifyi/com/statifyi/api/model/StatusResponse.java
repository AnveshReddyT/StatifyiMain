package statifyi.com.statifyi.api.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by KT on 27/12/15.
 */
public class StatusResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("icon")
    private String icon;

    @SerializedName("updatedTime")
    private Date updatedTime;

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

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
