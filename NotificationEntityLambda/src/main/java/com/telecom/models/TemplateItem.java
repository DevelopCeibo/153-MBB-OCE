package com.telecom.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TemplateItem {

    private String customObjectId;
    private List<CustomObjectFields> customObjectFieldsList;
    private String template;
    private String invoicesBaseUrl;

    // Constructores

    public TemplateItem() {}

    public TemplateItem(Map<String, Object> template ) {
        this.customObjectId = (String) template.getOrDefault("customObjectId", "186");
        this.customObjectFieldsList = (List<CustomObjectFields>) template.getOrDefault("customObjectFields", new ArrayList<CustomObjectFields>());
        this.template = (String) template.getOrDefault("template", "");
        this.invoicesBaseUrl = (String) template.getOrDefault("invoicesBaseUrl", "");
    }

    @Override
    public String toString() {
        return "{" +
                "customObjectId='" + customObjectId + '\'' +
                ", customObjectFieldsList=" + customObjectFieldsList +
                ", template='" + template + '\'' +
                ", invoicesBaseUrl='" + invoicesBaseUrl + '\'' +
                '}';
    }


    // Getters y Setters...

    public String getCustomObjectId() {
        return customObjectId;
    }

    public void setCustomObjectId(String customObjectId) {
        this.customObjectId = customObjectId;
    }


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

    public String getInvoicesBaseUrl() {
        return invoicesBaseUrl;
    }

    public void setInvoicesBaseUrl(String invoicesBaseUrl) {
        this.invoicesBaseUrl = invoicesBaseUrl;
    }
}

