package com.btg.fondos.integration;

import org.junit.jupiter.api.*;
import org.springframework.http.*;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Auth API - Pruebas de Integración")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthIntegrationTest extends BaseIntegrationTest {

    private static final String TEST_EMAIL = "auth-integration@test.com";
    private static final String TEST_PASSWORD = "password123";

    @Test
    @Order(1)
    @DisplayName("POST /api/auth/register - Debe registrar un cliente exitosamente")
    void shouldRegisterNewClient() {
        Map<String, Object> body = Map.of(
                "name", "Auth Test User",
                "email", TEST_EMAIL,
                "phone", "+573001111111",
                "password", TEST_PASSWORD,
                "notificationPreference", "EMAIL"
        );

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/auth/register", new HttpEntity<>(body, jsonHeaders()), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).containsKey("clientId");
        assertThat(response.getBody().get("email")).isEqualTo(TEST_EMAIL);
        assertThat(response.getBody().get("name")).isEqualTo("Auth Test User");
        assertThat(response.getBody()).containsKey("balance");
    }

    @Test
    @Order(2)
    @DisplayName("POST /api/auth/register - Debe fallar con email duplicado")
    void shouldFailRegisterWithDuplicateEmail() {
        Map<String, Object> body = Map.of(
                "name", "Otro Usuario",
                "email", TEST_EMAIL,
                "phone", "+573002222222",
                "password", "otropassword123",
                "notificationPreference", "SMS"
        );

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/auth/register", new HttpEntity<>(body, jsonHeaders()), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @Order(3)
    @DisplayName("POST /api/auth/login - Debe autenticar exitosamente")
    void shouldLoginSuccessfully() {
        Map<String, String> body = Map.of(
                "email", TEST_EMAIL,
                "password", TEST_PASSWORD
        );

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/auth/login", new HttpEntity<>(body, jsonHeaders()), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("token");
        assertThat(response.getBody().get("email")).isEqualTo(TEST_EMAIL);
        assertThat(response.getBody().get("name")).isEqualTo("Auth Test User");
        assertThat(response.getBody()).containsKey("clientId");
    }

    @Test
    @Order(4)
    @DisplayName("POST /api/auth/login - Debe fallar con contraseña incorrecta")
    void shouldFailLoginWithWrongPassword() {
        Map<String, String> body = Map.of(
                "email", TEST_EMAIL,
                "password", "wrongpassword"
        );

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/auth/login", new HttpEntity<>(body, jsonHeaders()), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @Order(5)
    @DisplayName("POST /api/auth/register - Debe fallar con datos inválidos (validación)")
    void shouldFailRegisterWithInvalidBody() {
        Map<String, Object> body = Map.of(
                "name", "",
                "email", "no-es-email",
                "phone", "",
                "password", "short"
        );

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/auth/register", new HttpEntity<>(body, jsonHeaders()), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @Order(6)
    @DisplayName("POST /api/auth/login - Debe fallar con email no registrado")
    void shouldFailLoginWithNonExistentEmail() {
        Map<String, String> body = Map.of(
                "email", "noexiste@test.com",
                "password", "password123"
        );

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/auth/login", new HttpEntity<>(body, jsonHeaders()), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
