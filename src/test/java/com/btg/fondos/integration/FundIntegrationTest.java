package com.btg.fondos.integration;

import org.junit.jupiter.api.*;
import org.springframework.http.*;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Fund API - Pruebas de Integración")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FundIntegrationTest extends BaseIntegrationTest {

    private static String authToken;

    @BeforeAll
    void setUpAuth() {
        authToken = registerAndLogin("Fund Test User", "fund-integration@test.com",
                "+573003333333", "fundpass123");
    }

    @Test
    @Order(1)
    @DisplayName("GET /api/funds - Debe listar los 5 fondos seeded (público)")
    void shouldListAllFundsPublicly() {
        ResponseEntity<List> response = restTemplate.getForEntity("/api/funds", List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(5);
    }

    @Test
    @Order(2)
    @DisplayName("POST /api/funds/1/subscribe - Debe suscribirse al fondo exitosamente")
    void shouldSubscribeToFundSuccessfully() {
        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/funds/1/subscribe",
                HttpMethod.POST,
                new HttpEntity<>(null, authHeaders(authToken)),
                Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().get("fundName")).isEqualTo("FPV_BTG_PACTUAL_RECAUDADORA");
        assertThat(response.getBody().get("type")).isEqualTo("OPENING");
        assertThat(response.getBody()).containsKey("transactionId");
    }

    @Test
    @Order(3)
    @DisplayName("POST /api/funds/1/subscribe - Debe fallar con suscripción duplicada")
    void shouldFailSubscribeDuplicate() {
        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/funds/1/subscribe",
                HttpMethod.POST,
                new HttpEntity<>(null, authHeaders(authToken)),
                Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @Order(4)
    @DisplayName("GET /api/funds/subscriptions - Debe retornar 1 suscripción activa")
    void shouldReturnOneActiveSubscription() {
        ResponseEntity<List> response = restTemplate.exchange(
                "/api/funds/subscriptions",
                HttpMethod.GET,
                new HttpEntity<>(null, authHeaders(authToken)),
                List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    @Order(5)
    @DisplayName("DELETE /api/funds/1/cancel - Debe cancelar suscripción y retornar monto")
    void shouldCancelSubscriptionSuccessfully() {
        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/funds/1/cancel",
                HttpMethod.DELETE,
                new HttpEntity<>(null, authHeaders(authToken)),
                Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("fundName")).isEqualTo("FPV_BTG_PACTUAL_RECAUDADORA");
        assertThat(response.getBody().get("type")).isEqualTo("CANCELLATION");
    }

    @Test
    @Order(6)
    @DisplayName("DELETE /api/funds/1/cancel - Debe fallar al cancelar suscripción inexistente")
    void shouldFailCancelNonExistentSubscription() {
        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/funds/1/cancel",
                HttpMethod.DELETE,
                new HttpEntity<>(null, authHeaders(authToken)),
                Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(7)
    @DisplayName("POST /api/funds/999/subscribe - Debe fallar con fondo inexistente")
    void shouldFailSubscribeToNonExistentFund() {
        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/funds/999/subscribe",
                HttpMethod.POST,
                new HttpEntity<>(null, authHeaders(authToken)),
                Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(8)
    @DisplayName("Debe fallar al suscribirse con saldo insuficiente")
    void shouldFailSubscribeWithInsufficientBalance() {
        // Suscribir a fondos caros para agotar el saldo (500,000 inicial)
        // Fondo 4: FDO-ACCIONES 250,000
        restTemplate.exchange("/api/funds/4/subscribe", HttpMethod.POST,
                new HttpEntity<>(null, authHeaders(authToken)), Map.class);
        // Fondo 2: FPV_BTG_PACTUAL_ECOPETROL 125,000
        restTemplate.exchange("/api/funds/2/subscribe", HttpMethod.POST,
                new HttpEntity<>(null, authHeaders(authToken)), Map.class);
        // Fondo 5: FPV_BTG_PACTUAL_DINAMICA 100,000
        restTemplate.exchange("/api/funds/5/subscribe", HttpMethod.POST,
                new HttpEntity<>(null, authHeaders(authToken)), Map.class);

        // Saldo restante: 500,000 - 250,000 - 125,000 - 100,000 = 25,000
        // Fondo 1: FPV_BTG_PACTUAL_RECAUDADORA requiere 75,000 → debe fallar
        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/funds/1/subscribe",
                HttpMethod.POST,
                new HttpEntity<>(null, authHeaders(authToken)),
                Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @Order(9)
    @DisplayName("POST /api/funds/1/subscribe - Debe fallar sin autenticación")
    void shouldFailSubscribeWithoutAuth() {
        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/funds/1/subscribe",
                HttpMethod.POST,
                new HttpEntity<>(null, jsonHeaders()),
                Map.class);

        assertThat(response.getStatusCode().value()).isIn(401, 403);
    }
}
