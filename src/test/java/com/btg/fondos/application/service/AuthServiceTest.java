package com.btg.fondos.application.service;

import com.btg.fondos.domain.exception.BusinessException;
import com.btg.fondos.domain.model.Client;
import com.btg.fondos.domain.model.NotificationType;
import com.btg.fondos.domain.model.Role;
import com.btg.fondos.domain.port.in.AuthUseCase;
import com.btg.fondos.domain.port.out.ClientRepository;
import com.btg.fondos.infrastructure.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private ClientRepository clientRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtProvider jwtProvider;

    private AuthService authService;

    @BeforeEach
    void setUp() throws Exception {
        authService = new AuthService(clientRepository, passwordEncoder, jwtProvider);
        var field = AuthService.class.getDeclaredField("initialBalance");
        field.setAccessible(true);
        field.set(authService, new BigDecimal("500000"));
    }

    @Nested
    @DisplayName("Registro de clientes")
    class RegisterTests {

        @Test
        @DisplayName("Debe registrar un nuevo cliente exitosamente")
        void shouldRegisterNewClient() {
            when(clientRepository.existsByEmail("juan@test.com")).thenReturn(false);
            when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
            when(clientRepository.save(any(Client.class))).thenAnswer(inv -> inv.getArgument(0));

            Client result = authService.register("Juan Pérez", "juan@test.com",
                    "+573001234567", "password123", NotificationType.EMAIL);

            assertThat(result.getName()).isEqualTo("Juan Pérez");
            assertThat(result.getEmail()).isEqualTo("juan@test.com");
            assertThat(result.getBalance()).isEqualByComparingTo(new BigDecimal("500000"));
            assertThat(result.getRole()).isEqualTo(Role.USER);
            assertThat(result.getPassword()).isEqualTo("encoded_password");
            assertThat(result.getNotificationPreference()).isEqualTo(NotificationType.EMAIL);
            assertThat(result.getClientId()).isNotBlank();

            verify(clientRepository).save(any(Client.class));
        }

        @Test
        @DisplayName("Debe lanzar excepción si el email ya está registrado")
        void shouldThrowWhenEmailAlreadyExists() {
            when(clientRepository.existsByEmail("juan@test.com")).thenReturn(true);

            assertThatThrownBy(() -> authService.register("Juan Pérez", "juan@test.com",
                    "+573001234567", "password123", NotificationType.EMAIL))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Ya existe un cliente registrado con el email");

            verify(clientRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe registrar con preferencia SMS")
        void shouldRegisterWithSmsPreference() {
            when(clientRepository.existsByEmail("maria@test.com")).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("encoded");
            when(clientRepository.save(any(Client.class))).thenAnswer(inv -> inv.getArgument(0));

            Client result = authService.register("María López", "maria@test.com",
                    "+573009876543", "password123", NotificationType.SMS);

            assertThat(result.getNotificationPreference()).isEqualTo(NotificationType.SMS);
            assertThat(result.getPhone()).isEqualTo("+573009876543");
        }
    }

    @Nested
    @DisplayName("Inicio de sesión")
    class LoginTests {

        private Client existingClient;

        @BeforeEach
        void setUp() {
            existingClient = Client.builder()
                    .clientId("client-1")
                    .name("Juan Pérez")
                    .email("juan@test.com")
                    .phone("+573001234567")
                    .balance(new BigDecimal("500000"))
                    .notificationPreference(NotificationType.EMAIL)
                    .password("encoded_password")
                    .role(Role.USER)
                    .build();
        }

        @Test
        @DisplayName("Debe iniciar sesión exitosamente con credenciales válidas")
        void shouldLoginSuccessfully() {
            when(clientRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(existingClient));
            when(passwordEncoder.matches("password123", "encoded_password")).thenReturn(true);
            when(jwtProvider.generateToken("client-1", "juan@test.com", "USER")).thenReturn("jwt-token-123");

            AuthUseCase.AuthResult result = authService.login("juan@test.com", "password123");

            assertThat(result.token()).isEqualTo("jwt-token-123");
            assertThat(result.client().getClientId()).isEqualTo("client-1");
            assertThat(result.client().getName()).isEqualTo("Juan Pérez");
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando el email no existe")
        void shouldThrowWhenEmailNotFound() {
            when(clientRepository.findByEmail("noexiste@test.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.login("noexiste@test.com", "password123"))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Credenciales inválidas");
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando la contraseña es incorrecta")
        void shouldThrowWhenPasswordIsWrong() {
            when(clientRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(existingClient));
            when(passwordEncoder.matches("wrong_password", "encoded_password")).thenReturn(false);

            assertThatThrownBy(() -> authService.login("juan@test.com", "wrong_password"))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Credenciales inválidas");

            verify(jwtProvider, never()).generateToken(anyString(), anyString(), anyString());
        }
    }
}
