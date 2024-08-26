package com.telecom.repository;

import com.telecom.models.EloquaAppItem;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class EloquaAppRepository {

    private final DynamoDbTable<EloquaAppItem> eloquaAppTable;

    // Constructor que inicializa el cliente y la tabla
    public EloquaAppRepository(DynamoDbClient dynamoDbClient) {
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
        this.eloquaAppTable = enhancedClient.table("EloquaAppTable", TableSchema.fromBean(EloquaAppItem.class));
    }

    // Método para guardar un item
    public void save(EloquaAppItem eloquaAppItem) {
        eloquaAppTable.putItem(eloquaAppItem);
    }

    // Método para obtener un item por su instanceId
    public EloquaAppItem getByInstanceId(String instanceId) {
        return eloquaAppTable.getItem(r -> r.key(k -> k.partitionValue(instanceId)));
    }
}

