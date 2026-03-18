package com.btg.fondos.integration;

import org.junit.jupiter.api.*;
import org.springframework.http.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.core.ParameterizedTypeReference;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Transaction API - Pruebas de Integración")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionIntegrationTest extends BaseIntegrationTest {

    private String authToken;

    @BeforeAll
    void setUpAuth() {
        authToken = registerAndLogin("Transaction Test User", "txn-integration@test.com",
                "+573004444444", "txnpass12345");
    }

    @Test
    @Order(1)
    @DisplayName("GET /api/transactions - Debe retornar historial vacío inicialmente")
    void shouldReturnEmptyHistoryInitially() {
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                "/api/transactions",
                HttpMethod.GET,
                new HttpEntity<>(null, authHeaders(authToken)),
                new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    @Order(2)
    @DisplayName("Debe generar transacción de apertura al suscribirse")
    void shouldCreateOpeningTransactionOnSubscribe() {
        // Subscribir al fondo 3 (DEUDAPRIVADA - $50,000)
        restTemplate.exchange("/api/funds/3/subscribe", HttpMethod.POST,
                new HttpEntity<>(null, authHeaders(authToken)),
                new ParameterizedTypeReference<Map<String, Object>>() {});

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                "/api/transactions",
                HttpMethod.GET,
                new HttpEntity<>(null, authHeaders(authToken)),
                new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Map<String, Object>> transactions = Objects.requireNonNull(response.getBody());
        assertThat(transactions).hasSize(1);

        assertThat(transactions.get(0))
                .containsEntry("type", "OPENING")
                .containsEntry("fundName", "DEUDAPRIVADA");

    }

    @Test
    @Order(3)
    @DisplayName("Debe generar transacción de cancelación al cancelar suscripción")
    void shouldCreateCancellationTransactionOnCancel() {
        // Cancelar suscripción al fondo 3
        restTemplate.exchange("/api/funds/3/cancel", HttpMethod.DELETE,
                new HttpEntity<>(null, authHeaders(authToken)),
                new ParameterizedTypeReference<Map<String, Object>>() {});

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                "/api/transactions",
                HttpMethod.GET,
                new HttpEntity<>(null, authHeaders(authToken)),
                new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    @Order(4)
    @DisplayName("GET /api/transactions - Debe fallar sin autenticación")
    void shouldFailWithoutAuth() {
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "/api/transactions",
                HttpMethod.GET,
                new HttpEntity<>(null, jsonHeaders()),
                new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
