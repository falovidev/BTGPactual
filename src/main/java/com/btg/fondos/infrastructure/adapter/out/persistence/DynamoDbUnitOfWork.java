package com.btg.fondos.infrastructure.adapter.out.persistence;

import com.btg.fondos.domain.common.port.UnitOfWork;
import com.btg.fondos.infrastructure.adapter.out.persistence.uow.EntityWriter;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.model.TransactWriteItemsEnhancedRequest;

import java.util.Map;

public class DynamoDbUnitOfWork implements UnitOfWork {

    private final DynamoDbEnhancedClient enhancedClient;
    private final Map<Class<?>, EntityWriter<?>> writers;
    private final TransactWriteItemsEnhancedRequest.Builder requestBuilder;

    public DynamoDbUnitOfWork(DynamoDbEnhancedClient enhancedClient,
                              Map<Class<?>, EntityWriter<?>> writers) {
        this.enhancedClient = enhancedClient;
        this.writers = writers;
        this.requestBuilder = TransactWriteItemsEnhancedRequest.builder();
    }

    @Override
    @SuppressWarnings("unchecked")
    public UnitOfWork save(Object domainEntity) {
        EntityWriter<Object> writer = (EntityWriter<Object>) writers.get(domainEntity.getClass());
        if (writer == null) {
            throw new IllegalArgumentException("No writer registered for: " + domainEntity.getClass().getSimpleName());
        }
        writer.addPut(requestBuilder, domainEntity);
        return this;
    }

    @Override
    public UnitOfWork delete(Class<?> entityType, Object... keys) {
        EntityWriter<?> writer = writers.get(entityType);
        if (writer == null) {
            throw new IllegalArgumentException("No writer registered for: " + entityType.getSimpleName());
        }
        writer.addDelete(requestBuilder, keys);
        return this;
    }

    @Override
    public void commit() {
        enhancedClient.transactWriteItems(requestBuilder.build());
    }
}
