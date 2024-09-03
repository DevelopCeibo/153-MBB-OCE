package com.telecom.services;

import com.telecom.models.PutResponse;
import com.telecom.models.RecordDefinition;

public class PutRequestService {

    public PutResponse buildPutRequestBody(String contactID, String emailAddress, String numeroIdentificacionCliente) {
        // Crear la instancia de RecordDefinitions
        RecordDefinition recordDefinition = new RecordDefinition(contactID, emailAddress, numeroIdentificacionCliente);

        // Crear la instancia de PutResponse
        PutResponse putResponse = new PutResponse();
        putResponse.setRecordDefinition(recordDefinition);
        putResponse.setHeight(256);
        putResponse.setWidth(256);
        putResponse.setEditorImageUrl("https://img04.en25.com/EloquaImages/clients/IngramMicroLATAM/%7B5f315c7b-a380-47af-95aa-140b9a43bd21%7D_32x32.png");
        putResponse.setRequiresConfiguration(false);

        return putResponse;
    }
}
