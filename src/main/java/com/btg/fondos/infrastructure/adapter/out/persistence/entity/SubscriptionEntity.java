package com.btg.fondos.infrastructure.adapter.out.persistence.entity;

import com.btg.fondos.domain.fund.model.Subscription;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.math.BigDecimal;

@DynamoDbBean
public class SubscriptionEntity {

    private String clientId;
    private String fundId;
    private String fundName;
    private BigDecimal amount;
    private String subscribedAt;

    @DynamoDbPartitionKey
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    @DynamoDbSortKey
    public String getFundId() { return fundId; }
    public void setFundId(String fundId) { this.fundId = fundId; }

    public String getFundName() { return fundName; }
    public void setFundName(String fundName) { this.fundName = fundName; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getSubscribedAt() { return subscribedAt; }
    public void setSubscribedAt(String subscribedAt) { this.subscribedAt = subscribedAt; }

    public static SubscriptionEntity fromDomain(Subscription subscription) {
        SubscriptionEntity entity = new SubscriptionEntity();
        entity.setClientId(subscription.getClientId());
        entity.setFundId(subscription.getFundId());
        entity.setFundName(subscription.getFundName());
        entity.setAmount(subscription.getAmount());
        entity.setSubscribedAt(subscription.getSubscribedAt().toString());
        return entity;
    }
}
