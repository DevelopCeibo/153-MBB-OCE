package com.telecom.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telecom.models.MessagePayload;

import java.util.Map;

public class RouterHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
    private final String queueUrl = System.getenv("QUEUE_URL");
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
        try {

            Map<String, String> queryStrings = apiGatewayProxyRequestEvent.getQueryStringParameters();
            String body = apiGatewayProxyRequestEvent.getBody();

            // Convertir el body de la solicitud a un Map<String, Object>
            Map<String, Object> bodyMap = objectMapper.readValue(body, Map.class);

            // Crear un objeto para representar el mensaje en formato JSON
            MessagePayload payload = new MessagePayload(queryStrings, bodyMap);

            // Convertir el objeto a JSON
            String messageBody = objectMapper.writeValueAsString(payload);

            // Enviar el mensaje a la cola SQS
            SendMessageRequest sendMessageRequest = new SendMessageRequest(queueUrl, messageBody);
            sqs.sendMessage(sendMessageRequest);

            // Devolver una respuesta de éxito
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody("Message sent to SQS successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(500)
                    .withBody("Failed to send message to SQS");
        }
    }
}
