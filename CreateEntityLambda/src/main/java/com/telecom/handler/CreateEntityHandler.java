package com.telecom.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telecom.models.RecordDefinitions;
import com.telecom.models.Response;

import java.util.Map;


public class CreateEntityHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {


    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
        // Obtener el logger del contexto
        LambdaLogger logger = context.getLogger();

        // Obtener los parámetros de la query
        Map<String, String> queryParams = apiGatewayProxyRequestEvent.getQueryStringParameters();

        // Obtener los parámetros con valores por defecto
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


        // Crea el objeto de respuesta
        Response response = new Response();
        response.setRecordDefinition(new RecordDefinitions("{{Contact.Id}}", "{{Contact.Field(C_EmailAddress)}}"));
        response.setHeight(256);
        response.setWidth(256);
        response.setEditorImageUrl("https://img04.en25.com/EloquaImages/clients/IngramMicroLATAM/%7B5f315c7b-a380-47af-95aa-140b9a43bd21%7D_32x32.png");
        response.setRequiresConfiguration(false);

        // Convierte el objeto a JSON
        ObjectMapper mapper = new ObjectMapper();
        String responseBody;
        try {
            responseBody = mapper.writeValueAsString(response);
        } catch (Exception e) {
            throw new RuntimeException("Error al convertir la respuesta a JSON", e);
        }

        // Crea la respuesta de API Gateway
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(responseBody)
                .withHeaders(Map.of("Content-Type", "application/json"));
    }

}
