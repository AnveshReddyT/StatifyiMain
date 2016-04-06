package statifyi.com.statifyi.api.model;

import statifyi.com.statifyi.utils.NetworkUtils;

/**
 * Created by KT on 06/04/16.
 */
public class CustomCall {

    private String mobile;
    private String message;
    private long time;

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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return NetworkUtils.provideGson().toJson(this);
    }
}
