package com.telecom.models;

import java.util.Map;

public class NotificationEntity {

    private Map<String, Object> data;

    public NotificationEntity(Map<String, Object> data) {
        this.data = data;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "NotificationEntity{" +
                "data: " + data +
                '}';
    }
}
