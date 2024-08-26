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
    private String oauthConsumerKey;
    private String oauthNonce;
    private String oauthSignatureMethod;
    private String oauthTimestamp;
    private String oauthVersion;
    private String oauthSignature;
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
        this.oauthConsumerKey = queryParams.getOrDefault("oauth_consumer_key", "");
        this.oauthNonce = queryParams.getOrDefault("oauth_nonce", "");
        this.oauthSignatureMethod = queryParams.getOrDefault("oauth_signature_method", "");
        this.oauthTimestamp = queryParams.getOrDefault("oauth_timestamp", "");
        this.oauthVersion = queryParams.getOrDefault("oauth_version", "");
        this.oauthSignature = queryParams.getOrDefault("oauth_signature", "");
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
                ", oauthConsumerKey='" + oauthConsumerKey + '\'' +
                ", oauthNonce='" + oauthNonce + '\'' +
                ", oauthSignatureMethod='" + oauthSignatureMethod + '\'' +
                ", oauthTimestamp='" + oauthTimestamp + '\'' +
                ", oauthVersion='" + oauthVersion + '\'' +
                ", oauthSignature='" + oauthSignature + '\'' +
                ", estate='" + state + '\'' +
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

    public String getOauthConsumerKey() {
        return oauthConsumerKey;
    }

    public void setOauthConsumerKey(String oauthConsumerKey) {
        this.oauthConsumerKey = oauthConsumerKey;
    }

    public String getOauthNonce() {
        return oauthNonce;
    }

    public void setOauthNonce(String oauthNonce) {
        this.oauthNonce = oauthNonce;
    }

    public String getOauthSignatureMethod() {
        return oauthSignatureMethod;
    }

    public void setOauthSignatureMethod(String oauthSignatureMethod) {
        this.oauthSignatureMethod = oauthSignatureMethod;
    }

    public String getOauthTimestamp() {
        return oauthTimestamp;
    }

    public void setOauthTimestamp(String oauthTimestamp) {
        this.oauthTimestamp = oauthTimestamp;
    }

    public String getOauthVersion() {
        return oauthVersion;
    }

    public void setOauthVersion(String oauthVersion) {
        this.oauthVersion = oauthVersion;
    }

    public String getOauthSignature() {
        return oauthSignature;
    }

    public void setOauthSignature(String oauthSignature) {
        this.oauthSignature = oauthSignature;
    }

    public String getEstate() {
        return state;
    }

    public void setEstate(String estate) {
        this.state = estate;
    }
}
