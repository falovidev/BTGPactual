package com.btg.fondos.integration;

import org.junit.jupiter.api.*;
import org.springframework.http.*;

import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;

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

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "/api/auth/register", HttpMethod.POST,
                new HttpEntity<>(body, jsonHeaders()),
                new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Map<String, Object> responseBody = response.getBody();
        assertThat(responseBody)
                .isNotNull()
                .containsKey("clientId")
                .containsEntry("email", TEST_EMAIL)
                .containsEntry("name", "Auth Test User")
                .containsKey("balance");
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

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "/api/auth/register", HttpMethod.POST,
                new HttpEntity<>(body, jsonHeaders()),
                new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull().containsEntry("error", "BUSINESS_ERROR");
    }

    @Test
    @Order(3)
    @DisplayName("POST /api/auth/login - Debe autenticar exitosamente")
    void shouldLoginSuccessfully() {
        Map<String, String> body = Map.of(
                "email", TEST_EMAIL,
                "password", TEST_PASSWORD
        );

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "/api/auth/login", HttpMethod.POST,
                new HttpEntity<>(body, jsonHeaders()),
                new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, Object> responseBody = response.getBody();
        assertThat(responseBody)
                .isNotNull()
                .containsKey("token")
                .containsEntry("email", TEST_EMAIL)
                .containsEntry("name", "Auth Test User")
                .containsKey("clientId");
    }

    @Test
    @Order(4)
    @DisplayName("POST /api/auth/login - Debe fallar con contraseña incorrecta")
    void shouldFailLoginWithWrongPassword() {
        Map<String, String> body = Map.of(
                "email", TEST_EMAIL,
                "password", "wrongpassword"
        );

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "/api/auth/login", HttpMethod.POST,
                new HttpEntity<>(body, jsonHeaders()),
                new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull().containsEntry("error", "BUSINESS_ERROR");
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

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "/api/auth/register", HttpMethod.POST,
                new HttpEntity<>(body, jsonHeaders()),
                new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull().containsEntry("error", "VALIDATION_ERROR");
    }

    @Test
    @Order(6)
    @DisplayName("POST /api/auth/login - Debe fallar con email no registrado")
    void shouldFailLoginWithNonExistentEmail() {
        Map<String, String> body = Map.of(
                "email", "noexiste@test.com",
                "password", "password123"
        );

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "/api/auth/login", HttpMethod.POST,
                new HttpEntity<>(body, jsonHeaders()),
                new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull().containsEntry("error", "BUSINESS_ERROR");
    }
}
