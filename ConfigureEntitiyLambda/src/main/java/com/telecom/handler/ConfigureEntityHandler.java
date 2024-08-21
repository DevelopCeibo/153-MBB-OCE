package com.telecom.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.telecom.models.PutResponse;
import com.telecom.models.RecordDefinition;
import com.telecom.services.PutRequestSenderService;
import com.telecom.services.PutRequestService;

import java.io.IOException;
import java.util.Map;

public class ConfigureEntityHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final PutRequestService putRequestService = new PutRequestService();
    private final PutRequestSenderService putRequestSenderService = new PutRequestSenderService();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {

        LambdaLogger logger = context.getLogger();
        String contactID = "{{Contact.Id}}"; // Solo ejemplos
        String emailAddress = "{{Contact.Field(C_EmailAddress)}}"; // Solo ejemplos


        Map<String, String> queryParams = apiGatewayProxyRequestEvent.getQueryStringParameters();
        var bodyString = apiGatewayProxyRequestEvent.getBody();

        //Logger de los parametros receibidos
        String instance_id = queryParams != null ? queryParams.getOrDefault("instance_id", "default") : "default";
        String install_id = queryParams != null ? queryParams.getOrDefault("install_id", "default") : "default";
        String user_name = queryParams != null ? queryParams.getOrDefault("user_name", "default") : "default";
        String user_id = queryParams != null ? queryParams.getOrDefault("user_id", "default") : "default";
        String site_name = queryParams != null ? queryParams.getOrDefault("site_name", "default") : "default";
        String site_id = queryParams != null ? queryParams.getOrDefault("site_id", "default") : "default";
        String app_id = queryParams != null ? queryParams.getOrDefault("app_id", "default") : "default";

        // Loguear los parámetros
        logger.log("instance_id: " + instance_id);
        logger.log("install_id: " + install_id);
        logger.log("user_name: " + user_name);
        logger.log("user_id: " + user_id);
        logger.log("site_name: " + site_name);
        logger.log("site_id: " + site_id);
        logger.log("app_id: " + app_id);
        logger.log(bodyString);

        // Acceder a la variable de entorno
        String eloquaToken = System.getenv("ELOQUA_AUTH_TOKEN");

        // Verificar si se ha obtenido la variable de entorno correctamente
        if (eloquaToken == null || eloquaToken.isEmpty()) {
            logger.log("Error: ELOQUA_AUTH_TOKEN no está definida.");
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(500)
                    .withBody("{Error : ELOQUA_AUTH_TOKEN no está definida}")
                    .withHeaders(Map.of("Content-Type", "application/json"))
                    ;


        }
        // Crear la URL para la solicitud PUT
        String url = "https://secure.p04.eloqua.com/api/cloud/1.0/contents/instances/" + instance_id;

        // Crear el objeto RecordDefinitions con los datos necesarios
        RecordDefinition recordDefinition = new RecordDefinition(contactID, emailAddress);

        // Crear el objeto PutResponse con los detalles de la solicitud PUT
        PutResponse putResponse = new PutResponse();
        putResponse.setRecordDefinition(recordDefinition);
        putResponse.setHeight(256);  // Ejemplo
        putResponse.setWidth(256);  // Ejemplo
        putResponse.setEditorImageUrl("https://img04.en25.com/EloquaImages/clients/IngramMicroLATAM/%7B5f315c7b-a380-47af-95aa-140b9a43bd21%7D_32x32.png");  // Ejemplo
        putResponse.setRequiresConfiguration(false);

        try {
            // Enviar la solicitud PUT
            String putResponseString = putRequestSenderService.sendPutRequest(url, putResponse, eloquaToken);
            logger.log("PUT Response: " + putResponseString);

            // Devolver respuesta exitosa
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody("{ \"message\": \"PUT request sent successfully\", \"response\": " + putResponseString + " }")
                    .withHeaders(Map.of("Content-Type", "application/json"));

        } catch (IOException e) {
            logger.log("Error during PUT request: " + e.getMessage());
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(500)
                    .withBody("{ \"Error\": \"Failed to send PUT request: " + e.getMessage() + "\" }")
                    .withHeaders(Map.of("Content-Type", "application/json"));
        }
    }
}
