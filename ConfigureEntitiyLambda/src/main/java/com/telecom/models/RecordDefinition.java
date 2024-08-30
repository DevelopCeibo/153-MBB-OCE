package com.telecom.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RecordDefinition {
    @JsonProperty("ContactID")
    private String contactID;

    @JsonProperty("EmailAddress")
    private String emailAddress;

    @JsonProperty("NUMERO_IDENTIFICACION_CLIENTE")
    private String numeroIdentificacionCliente;

    public RecordDefinition(String contactID, String emailAddress, String numeroIdentificacionCliente) {
        this.contactID = contactID;
        this.emailAddress = emailAddress;
        this.numeroIdentificacionCliente = numeroIdentificacionCliente;
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

    public String getNumeroIdentificacionCliente() {
        return numeroIdentificacionCliente;
    }

    public void setNumeroIdentificacionCliente(String numeroIdentificacionCliente) {
        this.numeroIdentificacionCliente = numeroIdentificacionCliente;
    }
}
