package com.btg.fondos.domain.auth.service;

import com.btg.fondos.domain.auth.port.PasswordEncoderPort;
import com.btg.fondos.domain.auth.port.TokenPort;
import com.btg.fondos.domain.client.model.Client;
import com.btg.fondos.domain.client.port.ClientRepository;
import com.btg.fondos.domain.exception.BusinessException;
import com.btg.fondos.domain.login.model.LoginResult;
import com.btg.fondos.domain.notification.model.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static com.btg.fondos.domain.testbuilder.ClientBuilder.aClient;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Servicio de autenticación de dominio")
class AuthDomainServiceTest {

    @Mock private ClientRepository clientRepository;
    @Mock private PasswordEncoderPort passwordEncoder;
    @Mock private TokenPort tokenPort;

    private AuthDomainService authDomainService;

    @BeforeEach
    void setUp() {
        authDomainService = new AuthDomainService(
                clientRepository, passwordEncoder, tokenPort, new BigDecimal("500000"));
    }

    // ==================== REGISTER ====================

    @Test
    @DisplayName("Debe registrar un cliente exitosamente")
    void shouldRegisterClientSuccessfully() {
        when(clientRepository.existsByEmail("juan@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-pass");
        when(clientRepository.save(any(Client.class))).thenAnswer(inv -> inv.getArgument(0));

        Client result = authDomainService.register(
                "Juan Pérez", "juan@test.com", "+573001234567",
                "password123", NotificationType.EMAIL);

        assertThat(result.getName()).isEqualTo("Juan Pérez");
        assertThat(result.getEmail()).isEqualTo("juan@test.com");
        assertThat(result.getBalance()).isEqualByComparingTo(new BigDecimal("500000"));
        assertThat(result.getPassword()).isEqualTo("encoded-pass");
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    @DisplayName("Debe fallar al registrar con email duplicado")
    void shouldFailWhenEmailAlreadyExists() {
        when(clientRepository.existsByEmail("juan@test.com")).thenReturn(true);

        assertThatThrownBy(() -> authDomainService.register(
                "Juan Pérez", "juan@test.com", "+573001234567",
                "password123", NotificationType.EMAIL))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Ya existe un cliente registrado");

        verify(clientRepository, never()).save(any());
    }

    // ==================== LOGIN ====================

    @Test
    @DisplayName("Debe autenticar exitosamente con credenciales válidas")
    void shouldLoginSuccessfully() {
        Client client = aClient().withPassword("encoded-pass").build();
        when(clientRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(client));
        when(passwordEncoder.matches("password123", "encoded-pass")).thenReturn(true);
        when(tokenPort.generateToken(anyString(), anyString(), anyString())).thenReturn("jwt-token");

        LoginResult result = authDomainService.login("juan@test.com", "password123");

        assertThat(result.token()).isEqualTo("jwt-token");
        assertThat(result.client().getEmail()).isEqualTo("juan@test.com");
    }

    @Test
    @DisplayName("Debe fallar con email no registrado")
    void shouldFailWhenEmailNotFound() {
        when(clientRepository.findByEmail("noexiste@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authDomainService.login("noexiste@test.com", "password123"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Credenciales inválidas");
    }

    @Test
    @DisplayName("Debe fallar con contraseña incorrecta")
    void shouldFailWhenPasswordDoesNotMatch() {
        Client client = aClient().withPassword("encoded-pass").build();
        when(clientRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(client));
        when(passwordEncoder.matches("wrong-pass", "encoded-pass")).thenReturn(false);

        assertThatThrownBy(() -> authDomainService.login("juan@test.com", "wrong-pass"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Credenciales inválidas");

        verify(tokenPort, never()).generateToken(anyString(), anyString(), anyString());
    }
}
