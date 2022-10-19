package com.cnt.police.network.requests;

public class NotificationRequest {
    private String to;
    private FcmDataPayload data;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public FcmDataPayload getData() {
        return data;
    }

    public void setData(FcmDataPayload data) {
        this.data = data;
    }
}
