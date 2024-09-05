package com.telecom.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.telecom.config.DynamoDbConfig;
import com.telecom.service.EloquaAppService;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.Map;

public class DeleteEntityHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final EloquaAppService eloquaAppService;

    public DeleteEntityHandler() {
        // Configura el cliente de DynamoDB y los servicios
        DynamoDbClient dynamoDbClient = DynamoDbConfig.dynamoDbClient();
        this.eloquaAppService = new EloquaAppService(dynamoDbClient);
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {

        LambdaLogger logger = context.getLogger();
        APIGatewayProxyResponseEvent response;

        Map<String, String> queryParams = apiGatewayProxyRequestEvent.getQueryStringParameters();

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

        try {
            eloquaAppService.deleteEloquaAppItemByInstanceId(instance_id);

            response = new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody("{ \"message\": \"DELETE request completed successfully\" }")
                    .withHeaders(Map.of("Content-Type", "application/json"));

        }catch (Exception e){

            logger.log("Error al intentar borrar el item " + e.getMessage());

            response = new APIGatewayProxyResponseEvent()
                    .withStatusCode(500)
                    .withBody("{ \"message\": \"Error occurred while trying to delete item with instance_id: " + instance_id + ". Details: " + e.getMessage() + "\" }")
                    .withHeaders(Map.of("Content-Type", "application/json"));
        }

        return response;
    }
}
