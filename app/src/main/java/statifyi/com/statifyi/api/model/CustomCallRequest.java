package statifyi.com.statifyi.api.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by KT on 04/04/16.
 */
public class CustomCallRequest {

    @SerializedName("mobile")
    private String mobile;

    @SerializedName("message")
    private String message;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
