package com.btg.fondos.domain.client.model;

import com.btg.fondos.domain.client.exception.InsufficientBalanceException;
import com.btg.fondos.domain.notification.model.NotificationType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.btg.fondos.domain.testbuilder.ClientBuilder.aClient;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Entidad Client")
class ClientTest {

    @Test
    @DisplayName("Debe retornar todos los campos correctamente")
    void shouldReturnAllFields() {
        Client client = aClient().build();

        assertThat(client.getClientId()).isEqualTo("client-1");
        assertThat(client.getName()).isEqualTo("Juan Pérez");
        assertThat(client.getEmail()).isEqualTo("juan@test.com");
        assertThat(client.getPhone()).isEqualTo("+573001234567");
        assertThat(client.getBalance()).isEqualByComparingTo(new BigDecimal("500000"));
        assertThat(client.getNotificationPreference()).isEqualTo(NotificationType.EMAIL);
        assertThat(client.getPassword()).isEqualTo("hashed-pass");
        assertThat(client.getRole()).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("Debe debitar cuando el saldo es mayor al monto")
    void shouldDebitWhenBalanceIsGreater() {
        Client client = aClient().withBalance("100000").build();

        client.debit(new BigDecimal("75000"), "FPV_RECAUDADORA");

        assertThat(client.getBalance()).isEqualByComparingTo(new BigDecimal("25000"));
    }

    @Test
    @DisplayName("Debe debitar cuando el saldo es exactamente igual al monto (caso borde)")
    void shouldDebitWhenBalanceEqualsAmount() {
        Client client = aClient().withBalance("75000").build();

        client.debit(new BigDecimal("75000"), "FPV_RECAUDADORA");

        assertThat(client.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el saldo es menor al monto")
    void shouldThrowWhenBalanceIsLessThanAmount() {
        Client client = aClient().withBalance("74999").build();

        assertThatThrownBy(() -> client.debit(new BigDecimal("75000"), "FPV_RECAUDADORA"))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessageContaining("FPV_RECAUDADORA");
    }

    @Test
    @DisplayName("Debe reembolsar correctamente")
    void shouldRefundCorrectly() {
        Client client = aClient().withBalance("425000").build();

        client.refund(new BigDecimal("75000"));

        assertThat(client.getBalance()).isEqualByComparingTo(new BigDecimal("500000"));
    }
}
