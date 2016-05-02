package statifyi.com.statifyi.model;

import com.google.gson.Gson;

/**
 * Created by KT on 02/05/16.
 */
public class Status {

    private String status;

    private String icon;

    private long date;

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

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
