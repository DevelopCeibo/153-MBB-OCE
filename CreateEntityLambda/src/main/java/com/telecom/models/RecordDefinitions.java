package com.telecom.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RecordDefinitions {
    @JsonProperty("ContactID")
    private String contactID;

    @JsonProperty("EmailAddress")
    private String emailAddress;

    public RecordDefinitions(String contactID, String emailAddress) {
        this.contactID = contactID;
        this.emailAddress = emailAddress;
    }

    public String getContactID() {
        return contactID;
    }

    public void setContactID(String contactID) {
        this.contactID = contactID;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
