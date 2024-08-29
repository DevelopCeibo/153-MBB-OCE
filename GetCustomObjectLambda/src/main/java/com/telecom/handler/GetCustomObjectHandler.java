package com.telecom.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class GetCustomObjectHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
        // Obtener el logger del contexto
        LambdaLogger logger = context.getLogger();

        // Inicializar la respuesta de la API
        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();

        // Agregar los headers de CORS
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Headers", "Content-Type");
        headers.put("Access-Control-Allow-Methods", "GET");

        responseEvent.setHeaders(headers);

        try {
            // Obtener los parámetros de la query
            Map<String, String> queryParams = apiGatewayProxyRequestEvent.getQueryStringParameters();
            String customObjectId = queryParams.get("id");
            if (customObjectId == null || customObjectId.isEmpty()) {
                responseEvent.setStatusCode(400);
                responseEvent.setBody("El parámetro 'id' es obligatorio.");
                return responseEvent;
            }

            // Obtener el token de autorización desde las variables de entorno
            String eloquaToken = System.getenv("eloquaToken");
            if (eloquaToken == null || eloquaToken.isEmpty()) {
                responseEvent.setStatusCode(500);
                responseEvent.setBody("Token de autorización no configurado.");
                return responseEvent;
            }

            // URL de la API Eloqua con el ID del custom object
            String url = "https://secure.p04.eloqua.com/api/rest/2.0/assets/customObject/" + customObjectId;
            logger.log("Realizando petición GET a: " + url);

            // Realizar la petición GET
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Basic " + eloquaToken);
            connection.setRequestProperty("Content-Type", "application/json");

            // Leer la respuesta de la API
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();

                // Establecer la respuesta con el contenido de la API de Eloqua
                responseEvent.setStatusCode(200);
                responseEvent.setBody(content.toString());
            } else {
                logger.log("Error en la petición a Eloqua: Código " + responseCode);
                responseEvent.setStatusCode(responseCode);
                responseEvent.setBody("Error al obtener el Custom Object de Eloqua.");
            }

            // Cerrar la conexión
            connection.disconnect();

        } catch (Exception e) {
            logger.log("Error en el handler: " + e.getMessage());
            responseEvent.setStatusCode(500);
            responseEvent.setBody("Error interno del servidor.");
        }

        return responseEvent;
    }
}

