package com.btg.fondos.domain.fund.model;

import java.math.BigDecimal;
import java.time.Instant;

public class Subscription {

    private final String clientId;
    private final String fundId;
    private final String fundName;
    private final BigDecimal amount;
    private final Instant subscribedAt;

    public Subscription(String clientId, String fundId, String fundName,
                        BigDecimal amount, Instant subscribedAt) {
        this.clientId = clientId;
        this.fundId = fundId;
        this.fundName = fundName;
        this.amount = amount;
        this.subscribedAt = subscribedAt;
    }

    //region Factory method

    public static Subscription create(String clientId, Fund fund) {
        return new Subscription(
                clientId, fund.getFundId(), fund.getName(),
                fund.getMinimumAmount(), Instant.now());
    }

    //endregion

    //region Getters

    public String getClientId() { return clientId; }
    public String getFundId() { return fundId; }
    public String getFundName() { return fundName; }
    public BigDecimal getAmount() { return amount; }
    public Instant getSubscribedAt() { return subscribedAt; }

    //endregion
}
