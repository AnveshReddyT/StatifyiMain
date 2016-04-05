package statifyi.com.statifyi.api.model;

/**
 * Created by KT on 27/12/15.
 */
public class StatusRequest {

    private String mobile;
    private String status;
    private String icon;

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
}
