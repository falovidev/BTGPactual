package com.btg.fondos.infrastructure.adapter.out.persistence.uow;

import com.btg.fondos.domain.client.model.Client;
import com.btg.fondos.infrastructure.adapter.out.persistence.entity.ClientEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.TransactWriteItemsEnhancedRequest;

@Component
@RequiredArgsConstructor
public class ClientEntityWriter implements EntityWriter<Client> {

    private final DynamoDbTable<ClientEntity> clientTable;

    @Override
    public Class<Client> supportedType() {
        return Client.class;
    }

    @Override
    public void addPut(TransactWriteItemsEnhancedRequest.Builder builder, Client client) {
        builder.addPutItem(clientTable, ClientEntity.fromDomain(client));
    }

    @Override
    public void addDelete(TransactWriteItemsEnhancedRequest.Builder builder, Object... keys) {
        builder.addDeleteItem(clientTable,
                Key.builder().partitionValue((String) keys[0]).build());
    }
}
