package com.btg.fondos.domain.testbuilder;

import com.btg.fondos.domain.transaction.model.Transaction;
import com.btg.fondos.domain.transaction.model.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class TransactionBuilder {

    private String transactionId = UUID.randomUUID().toString();
    private String clientId = "client-1";
    private String fundId = "1";
    private String fundName = "FPV_BTG_PACTUAL_RECAUDADORA";
    private TransactionType type = TransactionType.OPENING;
    private BigDecimal amount = new BigDecimal("75000");
    private Instant timestamp = Instant.now();

    public static TransactionBuilder aTransaction() {
        return new TransactionBuilder();
    }

    public TransactionBuilder withTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public TransactionBuilder withClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public TransactionBuilder withFundId(String fundId) {
        this.fundId = fundId;
        return this;
    }

    public TransactionBuilder withFundName(String fundName) {
        this.fundName = fundName;
        return this;
    }

    public TransactionBuilder withType(TransactionType type) {
        this.type = type;
        return this;
    }

    public TransactionBuilder withAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public TransactionBuilder withAmount(String amount) {
        this.amount = new BigDecimal(amount);
        return this;
    }

    public TransactionBuilder withTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public Transaction build() {
        return new Transaction(transactionId, clientId, fundId, fundName, type, amount, timestamp);
    }
}
