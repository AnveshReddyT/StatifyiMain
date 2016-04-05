package statifyi.com.statifyi.api.model;

import com.google.gson.Gson;

/**
 * Created by KT on 27/12/15.
 */
public class StatusResponse {

    private String status;
    private String icon;
    private String updatedTime;

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

    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
