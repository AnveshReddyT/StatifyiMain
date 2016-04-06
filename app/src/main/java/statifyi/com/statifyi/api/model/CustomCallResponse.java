package statifyi.com.statifyi.api.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by KT on 04/04/16.
 */
public class CustomCallResponse {

    @SerializedName("from")
    private String fromMobile;

    @SerializedName("message")
    private String message;

    public String getFromMobile() {
        return fromMobile;
    }

    public void setFromMobile(String fromMobile) {
        this.fromMobile = fromMobile;
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
