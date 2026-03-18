package com.btg.fondos.integration;

import org.junit.jupiter.api.*;
import org.springframework.http.*;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Transaction API - Pruebas de Integración")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionIntegrationTest extends BaseIntegrationTest {

    private static String authToken;

    @BeforeAll
    void setUpAuth() {
        authToken = registerAndLogin("Transaction Test User", "txn-integration@test.com",
                "+573004444444", "txnpass12345");
    }

    @Test
    @Order(1)
    @DisplayName("GET /api/transactions - Debe retornar historial vacío inicialmente")
    void shouldReturnEmptyHistoryInitially() {
        ResponseEntity<List> response = restTemplate.exchange(
                "/api/transactions",
                HttpMethod.GET,
                new HttpEntity<>(null, authHeaders(authToken)),
                List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    @Order(2)
    @DisplayName("Debe generar transacción de apertura al suscribirse")
    void shouldCreateOpeningTransactionOnSubscribe() {
        // Subscribir al fondo 3 (DEUDAPRIVADA - $50,000)
        restTemplate.exchange("/api/funds/3/subscribe", HttpMethod.POST,
                new HttpEntity<>(null, authHeaders(authToken)), Map.class);

        ResponseEntity<List> response = restTemplate.exchange(
                "/api/transactions",
                HttpMethod.GET,
                new HttpEntity<>(null, authHeaders(authToken)),
                List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);

        Map<String, Object> txn = (Map<String, Object>) response.getBody().get(0);
        assertThat(txn.get("type")).isEqualTo("OPENING");
        assertThat(txn.get("fundName")).isEqualTo("DEUDAPRIVADA");
    }

    @Test
    @Order(3)
    @DisplayName("Debe generar transacción de cancelación al cancelar suscripción")
    void shouldCreateCancellationTransactionOnCancel() {
        // Cancelar suscripción al fondo 3
        restTemplate.exchange("/api/funds/3/cancel", HttpMethod.DELETE,
                new HttpEntity<>(null, authHeaders(authToken)), Map.class);

        ResponseEntity<List> response = restTemplate.exchange(
                "/api/transactions",
                HttpMethod.GET,
                new HttpEntity<>(null, authHeaders(authToken)),
                List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    @Order(4)
    @DisplayName("GET /api/transactions - Debe fallar sin autenticación")
    void shouldFailWithoutAuth() {
        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/transactions",
                HttpMethod.GET,
                new HttpEntity<>(null, jsonHeaders()),
                Map.class);

        assertThat(response.getStatusCode().value()).isIn(401, 403);
    }
}
