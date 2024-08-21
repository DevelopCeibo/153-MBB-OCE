package com.telecom.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.telecom.models.NotificationEntity;
import com.telecom.services.NotificationEntityService;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class NotificationEntityHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {

        LambdaLogger logger = context.getLogger();

        NotificationEntityService notificationEntityService = new NotificationEntityService(logger);

        Map<String, String> queryStrings = apiGatewayProxyRequestEvent.getQueryStringParameters();
        String bodyString = apiGatewayProxyRequestEvent.getBody();
        Map<String, Object> body = notificationEntityService.convertJsonStringToMap(bodyString);


        NotificationEntity notificationEntity = new NotificationEntity(Map.of(
                "queryStrings", queryStrings,
                "body", body
        ));

        logger.log("notificationEntity: " + notificationEntity);

        try {
            notificationEntityService.processEloquaRequest(notificationEntity);
        } catch (Exception e) {
            context.getLogger().log("Error en el procesamiento: " + e.getMessage());
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(500)
                    .withBody("Error al procesar la solicitud.");
        }

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(204)
                .withHeaders(Map.of("Content-Type", "application/json"));
    }
}
