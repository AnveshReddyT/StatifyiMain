package statifyi.com.statifyi.api.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by KT on 12/04/16.
 */
public class MultiStatusResponse extends StatusResponse {

    @SerializedName("mobile")
    private String mobile;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public User toUser() {
        User user = new User();
        user.setMobile(this.getMobile());
        user.setStatus(this.getStatus());
        user.setIcon(this.getIcon());
        user.setUpdated(this.getUpdatedTime());
        return user;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
