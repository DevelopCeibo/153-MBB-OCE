package com.telecom.service;

import com.telecom.models.EloquaAppItem;
import com.telecom.repository.EloquaAppRepository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class EloquaAppService {

    private final EloquaAppRepository eloquaAppRepository;

    // Constructor que recibe el repositorio
    public EloquaAppService(DynamoDbClient dynamoDbClient) {
        this.eloquaAppRepository = new EloquaAppRepository(dynamoDbClient);
    }

    // Método para eliminar un item por instanceId
    public void deleteEloquaAppItemByInstanceId(String instanceId) {
        eloquaAppRepository.deleteByInstanceId(instanceId);
    }
}
