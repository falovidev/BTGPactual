package com.btg.fondos.domain.testbuilder;

import com.btg.fondos.domain.fund.model.Subscription;

import java.math.BigDecimal;
import java.time.Instant;

public class SubscriptionBuilder {

    private String clientId = "client-1";
    private String fundId = "1";
    private String fundName = "FPV_BTG_PACTUAL_RECAUDADORA";
    private BigDecimal amount = new BigDecimal("75000");
    private Instant subscribedAt = Instant.now();

    public static SubscriptionBuilder aSubscription() {
        return new SubscriptionBuilder();
    }

    public SubscriptionBuilder withClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public SubscriptionBuilder withFundId(String fundId) {
        this.fundId = fundId;
        return this;
    }

    public SubscriptionBuilder withFundName(String fundName) {
        this.fundName = fundName;
        return this;
    }

    public SubscriptionBuilder withAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public SubscriptionBuilder withAmount(String amount) {
        this.amount = new BigDecimal(amount);
        return this;
    }

    public SubscriptionBuilder withSubscribedAt(Instant subscribedAt) {
        this.subscribedAt = subscribedAt;
        return this;
    }

    public Subscription build() {
        return new Subscription(clientId, fundId, fundName, amount, subscribedAt);
    }
}
