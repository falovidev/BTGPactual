package com.btg.fondos.infrastructure.adapter.out.persistence.uow;

import com.btg.fondos.domain.fund.model.Subscription;
import com.btg.fondos.infrastructure.adapter.out.persistence.entity.SubscriptionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.TransactWriteItemsEnhancedRequest;

@Component
@RequiredArgsConstructor
public class SubscriptionEntityWriter implements EntityWriter<Subscription> {

    private final DynamoDbTable<SubscriptionEntity> subscriptionTable;

    @Override
    public Class<Subscription> supportedType() {
        return Subscription.class;
    }

    @Override
    public void addPut(TransactWriteItemsEnhancedRequest.Builder builder, Subscription subscription) {
        builder.addPutItem(subscriptionTable, SubscriptionEntity.fromDomain(subscription));
    }

    @Override
    public void addDelete(TransactWriteItemsEnhancedRequest.Builder builder, Object... keys) {
        builder.addDeleteItem(subscriptionTable,
                Key.builder().partitionValue((String) keys[0]).sortValue((String) keys[1]).build());
    }
}
