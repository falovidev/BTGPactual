package com.btg.fondos.integration;

import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local")
@Tag("integration")
public abstract class BaseIntegrationTest {

    static final GenericContainer<?> DYNAMODB;

    static {
        DYNAMODB = new GenericContainer<>("amazon/dynamodb-local:latest")
                .withExposedPorts(8000)
                .waitingFor(Wait.forHttp("/").forStatusCode(400));
        DYNAMODB.start();
    }

    @LocalServerPort
    protected int port;

    @Autowired
    protected TestRestTemplate restTemplate;

    @DynamicPropertySource
    static void dynamoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("aws.dynamodb.endpoint",
                () -> "http://localhost:" + DYNAMODB.getMappedPort(8000));
    }

    protected String registerAndLogin(String name, String email, String phone, String password) {
        Map<String, Object> registerBody = Map.of(
                "name", name,
                "email", email,
                "phone", phone,
                "password", password,
                "notificationPreference", "EMAIL"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        restTemplate.postForEntity("/api/auth/register",
                new HttpEntity<>(registerBody, headers), Map.class);

        Map<String, String> loginBody = Map.of(
                "email", email,
                "password", password
        );

        ResponseEntity<Map> loginResponse = restTemplate.postForEntity("/api/auth/login",
                new HttpEntity<>(loginBody, headers), Map.class);

        return (String) loginResponse.getBody().get("token");
    }

    protected HttpHeaders authHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        return headers;
    }

    protected HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
