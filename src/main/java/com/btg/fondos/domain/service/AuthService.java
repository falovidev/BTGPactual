package com.btg.fondos.domain.service;

import com.btg.fondos.domain.exception.BusinessException;
import com.btg.fondos.domain.model.Client;
import com.btg.fondos.domain.model.NotificationType;
import com.btg.fondos.domain.model.Role;
import com.btg.fondos.domain.port.in.AuthUseCase;
import com.btg.fondos.domain.port.out.ClientRepository;
import com.btg.fondos.infrastructure.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Value("${app.client.initial-balance}")
    private BigDecimal initialBalance;

    @Override
    public Client register(String name, String email, String phone,
                           String password, NotificationType notificationPreference) {
        if (clientRepository.existsByEmail(email)) {
            throw new BusinessException("Ya existe un cliente registrado con el email: " + email);
        }

        Client client = Client.builder()
                .clientId(UUID.randomUUID().toString())
                .name(name)
                .email(email)
                .phone(phone)
                .balance(initialBalance)
                .notificationPreference(notificationPreference)
                .password(passwordEncoder.encode(password))
                .role(Role.USER)
                .build();

        return clientRepository.save(client);
    }

    @Override
    public AuthResult login(String email, String password) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Credenciales inválidas"));

        if (!passwordEncoder.matches(password, client.getPassword())) {
            throw new BusinessException("Credenciales inválidas");
        }

        String token = jwtProvider.generateToken(client.getClientId(), client.getEmail(), client.getRole().name());
        return new AuthResult(token, client);
    }
}
