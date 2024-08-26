package com.telecom.service;

import com.telecom.models.EloquaAppItem;
import com.telecom.repository.EloquaAppRepository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class EloquaAppService {

    private final EloquaAppRepository eloquaAppRepository;

    public EloquaAppService(DynamoDbClient dynamoDbClient) {
        this.eloquaAppRepository = new EloquaAppRepository(dynamoDbClient);
    }

    public void saveEloquaApp(EloquaAppItem eloquaAppItem) {
        eloquaAppRepository.save(eloquaAppItem);
    }

    public EloquaAppItem getEloquaAppByInstanceId(String instanceId) {
        return eloquaAppRepository.getByInstanceId(instanceId);
    }
}

