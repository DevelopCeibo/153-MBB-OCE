package com.telecom.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import java.util.Map;

public class AuthorizerHandler implements RequestHandler<APIGatewayProxyRequestEvent, Map<String, Object>> {

    @Override
    public Map<String, Object> handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
        LambdaLogger logger = context.getLogger();

        try {
            // Obtener los query params
            Map<String, String> queryParams = apiGatewayProxyRequestEvent.getQueryStringParameters();
            String oauthConsumerKey = queryParams != null && queryParams.containsKey("oauth_consumer_key")
                    ? queryParams.get("oauth_consumer_key")
                    : "";
            logger.log("oauthConsumerKey: " + oauthConsumerKey);

            // Obtener la variable de entorno
            String eloquaConsumerKey = System.getenv("ELOQUA_CONSUMER_kEY");

            if (eloquaConsumerKey == null || eloquaConsumerKey.isEmpty()) {
                logger.log("Error: ELOQUA_CONSUMER_kEY no está definida.");
                throw new RuntimeException("ELOQUA_CONSUMER_kEY no está definida");
            }

            // Validar que el oauthConsumerKey coincida con eloquaConsumerKey
            if (!oauthConsumerKey.isEmpty() && oauthConsumerKey.equals(eloquaConsumerKey)) {
                // Autorización exitosa
                return generatePolicy("Allow", apiGatewayProxyRequestEvent.getRequestContext().getAccountId(), "execute-api:Invoke", apiGatewayProxyRequestEvent);
            } else {
                // Autorización fallida
                logger.log("Autorización fallida: oauth_consumer_key no coincide.");
                return generatePolicy("Deny", apiGatewayProxyRequestEvent.getRequestContext().getAccountId(), "execute-api:Invoke", apiGatewayProxyRequestEvent);
            }
        } catch (Exception e) {
            logger.log("Error interno: " + e.getMessage());
            throw new RuntimeException("Error de autorización: " + e.getMessage());
        }
    }

    private Map<String, Object> generatePolicy(String effect, String principalId, String action, APIGatewayProxyRequestEvent requestEvent) {
        String region = "us-east-1";
        String awsAccountId = requestEvent.getRequestContext().getAccountId();
        String apiId = requestEvent.getRequestContext().getApiId();
        String stage = requestEvent.getRequestContext().getStage();

        String resourceArn = String.format(
                "arn:aws:execute-api:%s:%s:%s/%s/*/*",
                region, awsAccountId, apiId, stage
        );

        // Crear la política
        Map<String, Object> policyDocument = Map.of(
                "Version", "2012-10-17",
                "Statement", new Object[]{
                        Map.of(
                                "Action", action,
                                "Effect", effect,
                                "Resource", resourceArn
                        )
                }
        );

        // Devolver el principal y la política
        return Map.of(
                "principalId", principalId,
                "policyDocument", policyDocument
        );
    }
}
