package statifyi.com.statifyi.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by KT on 27/12/15.
 */
public class RegisterUserRequest {

    @SerializedName("mobile")
    private String mobile;

    @SerializedName("countryCode")
    private String countryCode;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
