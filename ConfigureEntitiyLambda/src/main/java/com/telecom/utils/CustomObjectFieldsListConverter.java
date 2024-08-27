package com.telecom.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telecom.models.CustomObjectFields;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.io.IOException;
import java.util.List;

public class CustomObjectFieldsListConverter implements AttributeConverter<List<CustomObjectFields>> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public software.amazon.awssdk.services.dynamodb.model.AttributeValue transformFrom(List<CustomObjectFields> input) {
        try {
            return AttributeValue.builder()
                    .s(MAPPER.writeValueAsString(input))
                    .build();
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to convert List<CustomObjectFields> to string", e);
        }
    }

    @Override
    public List<CustomObjectFields> transformTo(AttributeValue attributeValue) {
        try {
            return MAPPER.readValue(attributeValue.s(), new TypeReference<List<CustomObjectFields>>() {});
        } catch (IOException e) {
            throw new IllegalStateException("Failed to convert string to List<CustomObjectFields>", e);
        }
    }

    @Override
    public EnhancedType<List<CustomObjectFields>> type() {
        return EnhancedType.listOf(CustomObjectFields.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S;
    }
}


