package com.btg.fondos.infrastructure.adapter.in.web.auth;

import com.btg.fondos.domain.exception.BusinessException;
import com.btg.fondos.domain.client.model.Client;
import com.btg.fondos.domain.login.model.LoginResult;
import com.btg.fondos.domain.notification.model.NotificationType;
import com.btg.fondos.domain.client.model.Role;
import com.btg.fondos.domain.client.port.ClientRepository;
import com.btg.fondos.infrastructure.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Value("${app.client.initial-balance}")
    private BigDecimal initialBalance;

    public Client register(String name, String email, String phone,
                           String password, NotificationType notificationPreference) {
        if (clientRepository.existsByEmail(email)) {
            throw new BusinessException("Ya existe un cliente registrado con el email: " + email);
        }

        Client client = new Client(
                UUID.randomUUID().toString(), name, email, phone,
                initialBalance, notificationPreference,
                passwordEncoder.encode(password), Role.USER);

        return clientRepository.save(client);
    }

    public LoginResult login(String email, String password) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Credenciales inválidas"));

        if (!passwordEncoder.matches(password, client.getPassword())) {
            throw new BusinessException("Credenciales inválidas");
        }

        String token = jwtProvider.generateToken(client.getClientId(), client.getEmail(), client.getRole().name());
        return new LoginResult(token, client);
    }
}
