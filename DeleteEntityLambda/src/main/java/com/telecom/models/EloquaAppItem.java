package com.telecom.models;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.Map;

@DynamoDbBean
public class EloquaAppItem {

    private String instanceId;
    private String installId;
    private String userName;
    private String userId;
    private String siteName;
    private String siteId;
    private String appId;
    private String state;

    // Constructores

    public EloquaAppItem() {}

    public EloquaAppItem(Map<String, String> queryParams) {
        this.instanceId = queryParams.getOrDefault("instance_id", "");
        this.installId = queryParams.getOrDefault("install_id", "");
        this.userName = queryParams.getOrDefault("user_name", "");
        this.userId = queryParams.getOrDefault("user_id", "");
        this.siteName = queryParams.getOrDefault("site_name", "");
        this.siteId = queryParams.getOrDefault("site_id", "");
        this.appId = queryParams.getOrDefault("app_id", "");
        this.state = queryParams.getOrDefault("state", "created");
    }

    @Override
    public String toString() {
        return "EloquaAppItem{" +
                "instanceId='" + instanceId + '\'' +
                ", installId='" + installId + '\'' +
                ", userName='" + userName + '\'' +
                ", userId='" + userId + '\'' +
                ", siteName='" + siteName + '\'' +
                ", siteId='" + siteId + '\'' +
                ", appId='" + appId + '\'' +
                ", state='" + state + '\'' +
                '}';
    }

    // Getters y Setters...

    @DynamoDbPartitionKey
    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getInstallId() {
        return installId;
    }

    public void setInstallId(String installId) {
        this.installId = installId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
