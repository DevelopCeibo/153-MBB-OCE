package com.telecom.services;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telecom.models.NotificationEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class NotificationEntityService {

    private final String eloquaToken = System.getenv("ELOQUA_AUTH_TOKEN");
    private final LambdaLogger logger;
    private ObjectMapper objectMapper = new ObjectMapper();

    public NotificationEntityService(LambdaLogger logger) {
        this.logger = logger;
    }

    public NotificationEntity createNotificationEntity(Map<String, Object> data) {
        // TODO - Guardar la entidad en la base de datos
        return new NotificationEntity(data);
    }

    public void processEloquaRequest(NotificationEntity notificationEntity) {
        Map<String, Object> data = notificationEntity.getData();
        String instanceId = ((String) ((Map<String, Object>) data.get("queryStrings")).get("instance_id")).replace("-", "");
        String executionId = (String) ((Map<String, Object>) data.get("queryStrings")).get("execution_id");
        String postBody = String.format("""
                {
                    "name": "Content Response Bulk Import",
                    "updateRule": "always",
                    "fields": {
                        "EmailAddress": "{{Contact.Field(C_EmailAddress)}}",
                        "Content": "{{ContentInstance(%s).Execution[%s]}}"
                    },
                    "identifierFieldName": "EmailAddress"
                }
                """, instanceId, executionId);

        String url = "https://secure.p04.eloqua.com/api/bulk/2.0/contacts/imports";

        try {
            String responseBody = makeHttpRequest(url, "POST", postBody);
            String importUri = extractUriFromResponse(responseBody);
            logger.log("importUri: " +importUri);
            String postDataBody = createPostDataBody(notificationEntity);
            logger.log("postDataBody: " + postDataBody);
            postToEloqua(importUri, postDataBody);
            syncImport(importUri);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String makeHttpRequest(String urlString, String method, String body) throws Exception {
        // Crear la URL y abrir la conexión
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Configurar el método de la solicitud (GET, POST, PUT, etc.)
        connection.setRequestMethod(method);
        connection.setRequestProperty("Authorization", "Basic " + eloquaToken);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        // Enviar el cuerpo de la solicitud si se proporciona
        if (body != null && !body.isEmpty()) {
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = body.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
        }

        // Obtener la respuesta del servidor
        int statusCode = connection.getResponseCode();
        if (statusCode >= 200 && statusCode < 300) {
            // Leer el cuerpo de la respuesta
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                return response.toString();
            }
        } else {
            // Manejar los códigos de error y leer el cuerpo de la respuesta de error si existe
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                StringBuilder errorResponse = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    errorResponse.append(responseLine.trim());
                }
                throw new Exception("Error en la solicitud HTTP: " + statusCode + " - " + errorResponse);
            }
        }
    }

    private String extractUriFromResponse(String responseBody) {
        logger.log(responseBody);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            return jsonNode.get("uri").asText();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al procesar la respuesta de Eloqua", e);
        }
    }

    private String createPostDataBody(NotificationEntity notificationEntity) {

            logger.log("createPostDataBody: " + notificationEntity.toString());
            Map<String, Object> body = (Map<String, Object>) notificationEntity.getData().get("body");
            logger.log("body: " + body);
            List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");
            logger.log("items: " + items);


            StringBuilder dataBuilder = new StringBuilder("[");

            for (Map<String, Object> item : items) {
                String contactId = (String) item.get("ContactID");
                String url = String.format("https://secure.p04.eloqua.com/api/rest/2.0/data/customObject/186/instances?search=contactId='%s'", contactId);

                try {
                    String responseBody = makeHttpRequest(url, "GET", null);
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode rootNode = objectMapper.readTree(responseBody);

                    JsonNode elements = rootNode.get("elements");
                    if (elements != null && elements.isArray()) {
                        StringBuilder contentBuilder = new StringBuilder();
                        for (JsonNode element : elements) {
                            String email = element.get("fieldValues").get(0).get("value").asText();
                            String contrato = element.get("fieldValues").get(1).get("value").asText();
                            String link = element.get("fieldValues").get(2).get("value").asText();
                            contentBuilder.append(String.format("<tr><td>%s</td><td>%s</td><td>%s</td></tr>", contrato, email, link));
                        }
                        String emailAddress = (String) item.get("EmailAddress");
                        dataBuilder.append(String.format("""
                                {
                                    "EmailAddress": "%s",
                                    "Content": "%s"
                                },
                                """, emailAddress, contentBuilder.toString()));
                    }

                } catch (Exception e) {
                    throw new RuntimeException("Error al procesar la respuesta de Eloqua", e);
                }
            }

        dataBuilder.setLength(dataBuilder.length() - 1);
        dataBuilder.append("]");
        return dataBuilder.toString();
    }

    private void postToEloqua(String importUri, String postDataBody) {
        String url = "https://secure.p04.eloqua.com/api/bulk/2.0" + importUri + "/data";
        try {
            String response = makeHttpRequest(url, "POST", postDataBody);
            System.out.println("Data Post Response: " + response);
        } catch (Exception e) {
            System.err.println("Error en la solicitud POST de datos: " + e.getMessage());
        }
    }

    private void syncImport(String importUri) {
        String syncBody = String.format("""
                {
                    "syncedInstanceURI": "%s"
                }
                """, importUri);

        String url = "https://secure.p04.eloqua.com/api/bulk/2.0/syncs";
        try {
            String response = makeHttpRequest(url, "POST", syncBody);
            System.out.println("Sincronización Response: " + response);
        } catch (Exception e) {
            System.err.println("Error en la sincronización: " + e.getMessage());
        }
    }

    public Map<String, Object> convertJsonStringToMap(String jsonString) {
        try {
            // Convertir el JSON a JsonNode
            JsonNode jsonNode = objectMapper.readTree(jsonString);
            // Convertir el JsonNode a Map
            return objectMapper.convertValue(jsonNode, Map.class);
        } catch (IOException e) {
            throw new RuntimeException("Error al convertir el JSON a Map", e);
        }
    }

}
