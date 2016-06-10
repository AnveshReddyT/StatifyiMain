package statifyi.com.statifyi.api.model;

import com.google.gson.Gson;

/**
 * Created by KT on 10/06/16.
 */
public class TopicMessageRequest {

    private String to;

    private TopicMessageData data;

    public TopicMessageData getData() {
        return data;
    }

    public void setData(TopicMessageData data) {
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}