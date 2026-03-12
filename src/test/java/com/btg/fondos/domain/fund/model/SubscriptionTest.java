package com.btg.fondos.domain.fund.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.btg.fondos.domain.testbuilder.FundBuilder.aFund;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Entidad Subscription")
class SubscriptionTest {

    @Test
    @DisplayName("Debe crear suscripción con factory method y retornar campos correctos")
    void shouldCreateSubscriptionViaFactory() {
        Fund fund = aFund().build();

        Subscription subscription = Subscription.create("c-1", fund);

        assertThat(subscription.getClientId()).isEqualTo("c-1");
        assertThat(subscription.getFundId()).isEqualTo("1");
        assertThat(subscription.getFundName()).isEqualTo("FPV_BTG_PACTUAL_RECAUDADORA");
        assertThat(subscription.getAmount()).isEqualByComparingTo(new BigDecimal("75000"));
        assertThat(subscription.getSubscribedAt()).isNotNull();
    }
}
