package com.telecom.models;

public class PutResponse {
    private RecordDefinition recordDefinition;
    private int height;
    private int width;
    private String editorImageUrl;
    private boolean requiresConfiguration;

    public RecordDefinition getRecordDefinition() {
        return recordDefinition;
    }

    public void setRecordDefinition(RecordDefinition recordDefinition) {
        this.recordDefinition = recordDefinition;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getEditorImageUrl() {
        return editorImageUrl;
    }

    public void setEditorImageUrl(String editorImageUrl) {
        this.editorImageUrl = editorImageUrl;
    }

    public boolean isRequiresConfiguration() {
        return requiresConfiguration;
    }

    public void setRequiresConfiguration(boolean requiresConfiguration) {
        this.requiresConfiguration = requiresConfiguration;
    }
}
