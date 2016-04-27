package statifyi.com.statifyi.api.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by KT on 27/12/15.
 */
public class StatusResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("name")
    private String name;

    @SerializedName("icon")
    private String icon;

    @SerializedName("updatedTime")
    private long updatedTime;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
