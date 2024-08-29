package com.telecom.config;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class DynamoDbConfig {

    private static final Region DEFAULT_REGION = Region.US_EAST_1;

    public static DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                .region(DEFAULT_REGION)
                .build();
    }
}
