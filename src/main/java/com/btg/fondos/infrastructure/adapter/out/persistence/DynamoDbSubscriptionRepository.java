package com.btg.fondos.infrastructure.adapter.out.persistence;

import com.btg.fondos.domain.model.Subscription;
import com.btg.fondos.domain.port.out.SubscriptionRepository;
import com.btg.fondos.infrastructure.adapter.out.persistence.entity.SubscriptionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DynamoDbSubscriptionRepository implements SubscriptionRepository {

    private final DynamoDbTable<SubscriptionEntity> subscriptionTable;

    @Override
    public Subscription save(Subscription subscription) {
        SubscriptionEntity entity = toEntity(subscription);
        subscriptionTable.putItem(entity);
        return subscription;
    }

    @Override
    public void delete(String clientId, String fundId) {
        subscriptionTable.deleteItem(
                Key.builder().partitionValue(clientId).sortValue(fundId).build());
    }

    @Override
    public Optional<Subscription> findByClientIdAndFundId(String clientId, String fundId) {
        SubscriptionEntity entity = subscriptionTable.getItem(
                Key.builder().partitionValue(clientId).sortValue(fundId).build());
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public List<Subscription> findByClientId(String clientId) {
        var results = subscriptionTable.query(QueryConditional.keyEqualTo(
                Key.builder().partitionValue(clientId).build()));

        return results.items().stream()
                .map(this::toDomain)
                .toList();
    }

    private SubscriptionEntity toEntity(Subscription subscription) {
        SubscriptionEntity entity = new SubscriptionEntity();
        entity.setClientId(subscription.getClientId());
        entity.setFundId(subscription.getFundId());
        entity.setFundName(subscription.getFundName());
        entity.setAmount(subscription.getAmount());
        entity.setSubscribedAt(subscription.getSubscribedAt().toString());
        return entity;
    }

    private Subscription toDomain(SubscriptionEntity entity) {
        return new Subscription(
                entity.getClientId(), entity.getFundId(), entity.getFundName(),
                new BigDecimal(entity.getAmount().toString()),
                Instant.parse(entity.getSubscribedAt()));
    }
}
