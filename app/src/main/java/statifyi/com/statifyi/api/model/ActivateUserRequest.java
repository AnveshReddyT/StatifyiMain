package statifyi.com.statifyi.api.model;

import com.google.gson.Gson;

/**
 * Created by KT on 28/12/15.
 */
public class ActivateUserRequest {

    private String mobile;
    private String code;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
