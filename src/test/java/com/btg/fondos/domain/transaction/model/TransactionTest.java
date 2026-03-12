package com.btg.fondos.domain.transaction.model;

import com.btg.fondos.domain.fund.model.Fund;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Entidad Transaction")
class TransactionTest {

    private final Fund fund = new Fund("f-1", "FPV_BTG_PACTUAL_RECAUDADORA",
            new BigDecimal("75000"), "FPV");

    @Test
    @DisplayName("Debe crear transacción de apertura con factory method")
    void shouldCreateOpeningTransaction() {
        Transaction tx = Transaction.createOpening("c-1", fund);

        assertThat(tx.getTransactionId()).isNotBlank();
        assertThat(tx.getClientId()).isEqualTo("c-1");
        assertThat(tx.getFundId()).isEqualTo("f-1");
        assertThat(tx.getFundName()).isEqualTo("FPV_BTG_PACTUAL_RECAUDADORA");
        assertThat(tx.getType()).isEqualTo(TransactionType.OPENING);
        assertThat(tx.getAmount()).isEqualByComparingTo(new BigDecimal("75000"));
        assertThat(tx.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Debe crear transacción de cancelación con factory method")
    void shouldCreateCancellationTransaction() {
        Transaction tx = Transaction.createCancellation("c-1", fund, new BigDecimal("75000"));

        assertThat(tx.getTransactionId()).isNotBlank();
        assertThat(tx.getClientId()).isEqualTo("c-1");
        assertThat(tx.getFundId()).isEqualTo("f-1");
        assertThat(tx.getFundName()).isEqualTo("FPV_BTG_PACTUAL_RECAUDADORA");
        assertThat(tx.getType()).isEqualTo(TransactionType.CANCELLATION);
        assertThat(tx.getAmount()).isEqualByComparingTo(new BigDecimal("75000"));
        assertThat(tx.getTimestamp()).isNotNull();
    }
}
