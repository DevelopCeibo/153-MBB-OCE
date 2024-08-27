package com.telecom.services;

import com.telecom.models.EloquaAppItem;
import com.telecom.repository.EloquaAppRepository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class EloquaAppService {

    private final EloquaAppRepository eloquaAppRepository;

    public EloquaAppService(DynamoDbClient dynamoDbClient) {
        this.eloquaAppRepository = new EloquaAppRepository(dynamoDbClient);
    }

    public EloquaAppItem getByInstanceId(String instanceId) {
        return eloquaAppRepository.getByInstanceId(instanceId);
    }

    public void updateItem(EloquaAppItem eloquaAppItem){
        eloquaAppRepository.updateItem(eloquaAppItem);
    }
}
