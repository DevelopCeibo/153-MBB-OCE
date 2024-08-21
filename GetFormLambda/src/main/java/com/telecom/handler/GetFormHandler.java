package com.telecom.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.util.Map;

public class GetFormHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
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

        // Construir el HTML
        String htmlResponse = "<!DOCTYPE html>" +
                "<html lang='en'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<title>Configure Entity Form</title>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; }" +
                "h2 { color: #333; }" +
                "form { background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); max-width: 400px; margin: auto; }" +
                "label { font-weight: bold; display: block; margin-bottom: 10px; }" +
                "input[type='text'] { width: 100%; padding: 8px; margin-bottom: 20px; border: 1px solid #ccc; border-radius: 4px; }" +
                "button { background-color: #4CAF50; color: white; padding: 10px 15px; border: none; border-radius: 4px; cursor: pointer; }" +
                "button:hover { background-color: #45a049; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<h2>Configure Entity Form</h2>" +
                "<form id='configForm'>" +
                "<label for='name'>Nombre:</label>" +
                "<input type='text' id='name' name='name'>" +
                "<button type='submit'>Enviar</button>" +
                "</form>" +
                "<script>" +
                "document.getElementById('configForm').addEventListener('submit', function(event) {" +
                "    event.preventDefault();" +  // Previene el comportamiento por defecto del formulario
                "    const formData = new FormData(event.target);" +
                "    const jsonData = JSON.stringify(Object.fromEntries(formData));" +  // Serializa a JSON
                "    fetch('https://p3tg75utdi.execute-api.us-east-1.amazonaws.com/configure_entities" +
                "?instance_id=" + instance_id +
                "&install_id=" + install_id +
                "&user_name=" + user_name +
                "&user_id=" + user_id +
                "&site_name=" + site_name +
                "&site_id=" + site_id +
                "&app_id=" + app_id +
                "', {" +
                "        method: 'POST'," +
                "        headers: {" +
                "            'Content-Type': 'application/json'" +
                "        }," +
                "        body: jsonData" +  // Envía los datos como JSON
                "    }).then(response => response.json())" +
                "      .then(data => console.log(data));" +
                "});" +
                "</script>" +
                "</body>" +
                "</html>";

        // Crear la respuesta
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(200);
        response.setHeaders(Map.of("Content-Type", "text/html"));
        response.setBody(htmlResponse);

        return response;
    }
}
