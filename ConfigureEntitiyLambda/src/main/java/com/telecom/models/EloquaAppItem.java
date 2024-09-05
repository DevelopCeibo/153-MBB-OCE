package com.telecom.models;

import com.telecom.utils.CustomObjectFieldsListConverter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.ArrayList;
import java.util.List;
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
    private String customObjectId;
    private List<CustomObjectFields> customObjectFieldsList;
    private String template;
    private String pivotField;

    // Constructores

    public EloquaAppItem() {}

    public EloquaAppItem(Map<String, Object> queryParams) {
        this.instanceId = (String) queryParams.getOrDefault("instance_id", "");
        this.installId = (String) queryParams.getOrDefault("install_id", "");
        this.userName = (String) queryParams.getOrDefault("user_name", "");
        this.userId = (String) queryParams.getOrDefault("user_id", "");
        this.siteName = (String) queryParams.getOrDefault("site_name", "");
        this.siteId = (String) queryParams.getOrDefault("site_id", "");
        this.appId = (String) queryParams.getOrDefault("app_id", "");
        this.state = (String) queryParams.getOrDefault("state", "configurated");
        this.customObjectId = (String) queryParams.getOrDefault("customObjectId", "186");
        this.customObjectFieldsList = (List<CustomObjectFields>) queryParams.getOrDefault("customObjectFields", new ArrayList<CustomObjectFields>());
        this.template = (String) queryParams.getOrDefault("template", "");
        this.pivotField = (String) queryParams.getOrDefault("pivotField", "");
    }

    public EloquaAppItem(Map<String, String> queryParams, Map<String, Object> body) {
        this.instanceId = queryParams.getOrDefault("instance_id", "");
        this.installId = queryParams.getOrDefault("install_id", "");
        this.userName = queryParams.getOrDefault("user_name", "");
        this.userId = queryParams.getOrDefault("user_id", "");
        this.siteName = queryParams.getOrDefault("site_name", "");
        this.siteId = queryParams.getOrDefault("site_id", "");
        this.appId = queryParams.getOrDefault("app_id", "");

        this.state = queryParams.getOrDefault("state", "created");
        this.customObjectId = (String) body.getOrDefault("customObjectId", "186");
        this.customObjectFieldsList = (List<CustomObjectFields>) body.getOrDefault("customObjectFields", new ArrayList<CustomObjectFields>());
        this.template = (String) body.getOrDefault("template", "");
        this.pivotField = (String) body.getOrDefault("pivotField", "");
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
                ", customObjectId='" + customObjectId + '\'' +
                ", customObjectFieldsList=" + customObjectFieldsList +
                ", template='" + template + '\'' +
                ", pivotField='" + pivotField + '\'' +
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

    public String getCustomObjectId() {
        return customObjectId;
    }

    public void setCustomObjectId(String customObjectId) {
        this.customObjectId = customObjectId;
    }

    @DynamoDbConvertedBy(CustomObjectFieldsListConverter.class)
    public List<CustomObjectFields> getCustomObjectFieldsList() {
        return customObjectFieldsList;
    }

    public void setCustomObjectFieldsList(List<CustomObjectFields> customObjectFieldsList) {
        this.customObjectFieldsList = customObjectFieldsList;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getPivotField() {
        return pivotField;
    }

    public void setPivotField(String pivotField) {
        this.pivotField = pivotField;
    }
}
