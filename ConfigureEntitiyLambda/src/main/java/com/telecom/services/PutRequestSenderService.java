package com.telecom.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telecom.models.PutResponse;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;

public class PutRequestSenderService {
    private final ObjectMapper objectMapper;

    public PutRequestSenderService() {
        this.objectMapper = new ObjectMapper();
    }

    public String sendPutRequest(String url, PutResponse putResponse, String eloquaToken) throws IOException {
        // Serializar el cuerpo a JSON
        String jsonBody = serializeToJson(putResponse);

        // Crear el cliente HTTP
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPut request = new HttpPut(url);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Authorization", "Basic " + eloquaToken);

            // Agregar el cuerpo de la solicitud
            StringEntity entity = new StringEntity(jsonBody);
            request.setEntity(entity);

            // Ejecutar la solicitud
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                // Obtener la respuesta en formato String
                return EntityUtils.toString(response.getEntity());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String serializeToJson(PutResponse putResponse) throws JsonProcessingException {
        return objectMapper.writeValueAsString(putResponse);
    }
}
