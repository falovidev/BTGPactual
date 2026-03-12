package com.btg.fondos.infrastructure.adapter.out.persistence.entity;

import com.btg.fondos.domain.transaction.model.Transaction;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

import java.math.BigDecimal;

@DynamoDbBean
public class TransactionEntity {

    private String transactionId;
    private String clientId;
    private String fundId;
    private String fundName;
    private String type;
    private BigDecimal amount;
    private String timestamp;

    @DynamoDbPartitionKey
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    @DynamoDbSecondaryPartitionKey(indexNames = "clientId-index")
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getFundId() { return fundId; }
    public void setFundId(String fundId) { this.fundId = fundId; }

    public String getFundName() { return fundName; }
    public void setFundName(String fundName) { this.fundName = fundName; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public static TransactionEntity fromDomain(Transaction transaction) {
        TransactionEntity entity = new TransactionEntity();
        entity.setTransactionId(transaction.getTransactionId());
        entity.setClientId(transaction.getClientId());
        entity.setFundId(transaction.getFundId());
        entity.setFundName(transaction.getFundName());
        entity.setType(transaction.getType().name());
        entity.setAmount(transaction.getAmount());
        entity.setTimestamp(transaction.getTimestamp().toString());
        return entity;
    }
}
