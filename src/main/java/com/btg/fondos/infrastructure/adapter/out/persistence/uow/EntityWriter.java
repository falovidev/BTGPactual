package com.btg.fondos.infrastructure.adapter.out.persistence.uow;

import software.amazon.awssdk.enhanced.dynamodb.model.TransactWriteItemsEnhancedRequest;

public interface EntityWriter<D> {

    Class<D> supportedType();

    void addPut(TransactWriteItemsEnhancedRequest.Builder builder, D domainEntity);

    void addDelete(TransactWriteItemsEnhancedRequest.Builder builder, Object... keys);
}
