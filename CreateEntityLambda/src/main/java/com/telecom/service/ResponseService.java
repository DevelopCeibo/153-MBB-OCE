package com.telecom.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telecom.models.Response;
import com.telecom.models.RecordDefinitions;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.util.Map;

public class ResponseService {

    private final ObjectMapper mapper = new ObjectMapper();

    public APIGatewayProxyResponseEvent createEloquaResponse() {
        // Crear el objeto de respuesta
        Response response = new Response();
        response.setRecordDefinition(new RecordDefinitions("{{Contact.Id}}", "{{Contact.Field(C_EmailAddress)}}", "{{Contact.Field(C_NUMERO_IDENTIFICACION_CLIENTE1)}}"));
        response.setHeight(256);
        response.setWidth(256);
        response.setEditorImageUrl("https://img04.en25.com/EloquaImages/clients/IngramMicroLATAM/%7B5f315c7b-a380-47af-95aa-140b9a43bd21%7D_32x32.png");
        response.setRequiresConfiguration(false);

        // Convertir el objeto a JSON
        String responseBody;
        try {
            responseBody = mapper.writeValueAsString(response);
        } catch (Exception e) {
            throw new RuntimeException("Error al convertir la respuesta a JSON", e);
        }

        // Crear la respuesta de API Gateway
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(responseBody)
                .withHeaders(Map.of("Content-Type", "application/json"));
    }
}

