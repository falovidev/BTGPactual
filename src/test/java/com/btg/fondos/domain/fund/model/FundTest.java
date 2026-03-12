package com.btg.fondos.domain.fund.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Entidad Fund")
class FundTest {

    @Test
    @DisplayName("Debe retornar todos los campos correctamente")
    void shouldReturnAllFields() {
        Fund fund = new Fund("f-1", "FPV_BTG_PACTUAL_RECAUDADORA", new BigDecimal("75000"), "FPV");

        assertThat(fund.getFundId()).isEqualTo("f-1");
        assertThat(fund.getName()).isEqualTo("FPV_BTG_PACTUAL_RECAUDADORA");
        assertThat(fund.getMinimumAmount()).isEqualByComparingTo(new BigDecimal("75000"));
        assertThat(fund.getCategory()).isEqualTo("FPV");
    }
}
