package com.telecom.models;

import java.util.Map;

public class MessagePayload {
    private Map<String, String> requestQueryStrings;
    private Map<String, Object> requestBody;

    public MessagePayload(Map<String, String> requestQueryStrings, Map<String, Object> requestBody) {
        this.requestQueryStrings = requestQueryStrings;
        this.requestBody = requestBody;
    }

    public Map<String, String> getRequestQueryStrings() {
        return requestQueryStrings;
    }

    public void setRequestQueryStrings(Map<String, String> requestQueryStrings) {
        this.requestQueryStrings = requestQueryStrings;
    }

    public Map<String, Object> getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(Map<String, Object> requestBody) {
        this.requestBody = requestBody;
    }
}
