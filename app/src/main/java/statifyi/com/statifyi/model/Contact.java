package statifyi.com.statifyi.model;

import com.google.gson.Gson;

/**
 * Created by KT on 12/7/15.
 */
public class Contact {

    private String name;
    private String mobile;
    private String photo;

    public Contact() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
