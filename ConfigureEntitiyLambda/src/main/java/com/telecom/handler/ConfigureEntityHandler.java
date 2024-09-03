package com.telecom.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telecom.config.DynamoDbConfig;
import com.telecom.models.CustomObjectFields;
import com.telecom.models.EloquaAppItem;
import com.telecom.services.EloquaAppService;
import com.telecom.services.PutRequestSenderService;
import com.telecom.services.PutRequestService;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ConfigureEntityHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final EloquaAppService eloquaAppService;
    private final PutRequestService putRequestService = new PutRequestService();
    private final PutRequestSenderService putRequestSenderService = new PutRequestSenderService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ConfigureEntityHandler() {
        // Configura el cliente de DynamoDB y los servicios
        DynamoDbClient dynamoDbClient = DynamoDbConfig.dynamoDbClient();
        this.eloquaAppService = new EloquaAppService(dynamoDbClient);
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {

        LambdaLogger logger = context.getLogger();
        Map<String, Object> body;

        String contactID = "{{Contact.Id}}";
        String emailAddress = "{{Contact.Field(C_EmailAddress)}}";
        String numeroIdentifiacionCliente = "{{Contact.Field(C_NUMERO_IDENTIFICACION_CLIENTE1)}}";


        Map<String, String> queryParams = apiGatewayProxyRequestEvent.getQueryStringParameters();
        var bodyString = apiGatewayProxyRequestEvent.getBody();



        String instance_id = queryParams.getOrDefault("instance_id", "");
        if(instance_id == ""){
            logger.log("Error: instance_id no está definida en los Query Params.");
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(500)
                    .withBody("{Error : instance_id no está definida en los Query Params}")
                    .withHeaders(Map.of("Content-Type", "application/json"));
        }

        EloquaAppItem eloquaAppItem = eloquaAppService.getByInstanceId(instance_id);

        logger.log(eloquaAppItem.toString());

        try{
            body = objectMapper.readValue(bodyString, Map.class);
            eloquaAppItem.setCustomObjectId((String) body.get("customObjectId"));
            String customObjectFieldsStrings =  (String) body.get("customObjectFields");
            List<CustomObjectFields> customObjectFields = objectMapper.readValue(customObjectFieldsStrings, List.class);
            eloquaAppItem.setCustomObjectFieldsList( customObjectFields);  //customObjectFields
            eloquaAppItem.setTemplate((String) body.get("template"));
            eloquaAppItem.setInvoicesBaseUrl((String) body.get("invoicesBaseUrl"));
            eloquaAppItem.setState("updated");
            eloquaAppItem.setPivotField((String) body.get("pivotField"));

            eloquaAppService.updateItem(eloquaAppItem);

        }catch (Exception e){
            logger.log("Error al actualizar item en la base de datos : " + e.getMessage());
        }


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
       var putResponse = putRequestService.buildPutRequestBody(contactID, emailAddress, numeroIdentifiacionCliente);

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
