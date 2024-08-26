package com.telecom.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.telecom.config.DynamoDbConfig;
import com.telecom.models.EloquaAppItem;
import com.telecom.service.EloquaAppService;
import com.telecom.service.ResponseService;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.Map;


public class CreateEntityHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final EloquaAppService eloquaAppService;
    private final ResponseService responseService;

    public CreateEntityHandler() {
        // Configura el cliente de DynamoDB y los servicios
        DynamoDbClient dynamoDbClient = DynamoDbConfig.dynamoDbClient();
        this.eloquaAppService = new EloquaAppService(dynamoDbClient);
        this.responseService = new ResponseService();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
        // Obtener el logger del contexto
        LambdaLogger logger = context.getLogger();

        // Obtener los parámetros de la query
        Map<String, String> queryParams = apiGatewayProxyRequestEvent.getQueryStringParameters();

        // Crear el objeto EloquaAppItem usando el constructor con Map
        EloquaAppItem eloquaAppItem = new EloquaAppItem(queryParams);

        // Loguear los parámetros
        logger.log(eloquaAppItem.toString());

        // Guardar el item en DynamoDB
        eloquaAppService.saveEloquaApp(eloquaAppItem);

        // Crear la respuesta de API Gateway usando ResponseService
        return responseService.createEloquaResponse();
    }

}
