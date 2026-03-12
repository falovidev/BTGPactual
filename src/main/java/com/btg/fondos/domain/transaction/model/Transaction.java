package com.btg.fondos.domain.transaction.model;

import com.btg.fondos.domain.fund.model.Fund;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Transaction {

    private final String transactionId;
    private final String clientId;
    private final String fundId;
    private final String fundName;
    private final TransactionType type;
    private final BigDecimal amount;
    private final Instant timestamp;

    public Transaction(String transactionId, String clientId, String fundId,
                       String fundName, TransactionType type, BigDecimal amount, Instant timestamp) {
        this.transactionId = transactionId;
        this.clientId = clientId;
        this.fundId = fundId;
        this.fundName = fundName;
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    //region Factory methods

    public static Transaction createOpening(String clientId, Fund fund) {
        return new Transaction(
                UUID.randomUUID().toString(), clientId, fund.getFundId(),
                fund.getName(), TransactionType.OPENING, fund.getMinimumAmount(), Instant.now());
    }

    public static Transaction createCancellation(String clientId, Fund fund, BigDecimal amount) {
        return new Transaction(
                UUID.randomUUID().toString(), clientId, fund.getFundId(),
                fund.getName(), TransactionType.CANCELLATION, amount, Instant.now());
    }

    //endregion

    //region Getters

    public String getTransactionId() { return transactionId; }
    public String getClientId() { return clientId; }
    public String getFundId() { return fundId; }
    public String getFundName() { return fundName; }
    public TransactionType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public Instant getTimestamp() { return timestamp; }

    //endregion
}
