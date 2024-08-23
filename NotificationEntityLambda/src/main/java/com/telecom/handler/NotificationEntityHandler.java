package com.telecom.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telecom.models.NotificationEntity;
import com.telecom.services.NotificationEntityService;

import java.util.Map;

public class NotificationEntityHandler implements RequestHandler<SQSEvent, Void> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Void handleRequest(SQSEvent sqsEvent, Context context) {

        LambdaLogger logger = context.getLogger();

        NotificationEntityService notificationEntityService = new NotificationEntityService(logger);

        // Recorrer los mensajes recibidos en el evento de SQS
        for (SQSEvent.SQSMessage message : sqsEvent.getRecords()) {
            try {
                // El cuerpo del mensaje de SQS está en formato JSON, que incluye los queryStrings y el body.
                String messageBody = message.getBody();

                // Deserializar el mensaje para obtener los queryStrings y el body
                Map<String, Object> messageMap = objectMapper.readValue(messageBody, Map.class);
                Map<String, String> queryStrings = (Map<String, String>) messageMap.get("requestQueryStrings");
                Map<String, Object> body = (Map<String, Object>) messageMap.get("requestBody");

                // Crear la entidad NotificationEntity con los datos extraídos
                NotificationEntity notificationEntity = new NotificationEntity(Map.of(
                        "queryStrings", queryStrings,
                        "body", body
                ));

                logger.log("notificationEntity: " + notificationEntity);

                // Procesar la solicitud utilizando NotificationEntityService
                notificationEntityService.processEloquaRequest(notificationEntity);

            } catch (Exception e) {
                logger.log("Error procesando el mensaje de SQS: " + e.getMessage());
            }
        }

        // No necesitamos devolver una respuesta, ya que el evento de SQS no requiere un APIGatewayProxyResponseEvent
        return null;
    }
}
