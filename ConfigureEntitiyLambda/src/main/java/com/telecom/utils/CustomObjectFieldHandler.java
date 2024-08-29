package com.telecom.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;


public class CustomObjectFieldHandler {
    public void handleCustomObjectFields(String customObjectFieldsJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Deserializar el JSON a una lista de mapas (o puedes crear una clase específica)
            List<Map<String, Object>> customObjectFields = objectMapper.readValue(customObjectFieldsJson, List.class);

            // Procesar la lista
            for (Map<String, Object> field : customObjectFields) {
                System.out.println("ID: " + field.get("id"));
                System.out.println("Name: " + field.get("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al deserializar customObjectFields");
        }
    }
}
