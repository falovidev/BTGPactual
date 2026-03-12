package com.btg.fondos.infrastructure.config;

import com.btg.fondos.domain.auth.port.PasswordEncoderPort;
import com.btg.fondos.domain.auth.port.TokenPort;
import com.btg.fondos.domain.auth.service.AuthDomainService;
import com.btg.fondos.domain.client.port.ClientRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class AuthConfig {

    @Bean
    public AuthDomainService authDomainService(ClientRepository clientRepository,
                                               PasswordEncoderPort passwordEncoder,
                                               TokenPort tokenPort,
                                               @Value("${app.client.initial-balance}") BigDecimal initialBalance) {
        return new AuthDomainService(clientRepository, passwordEncoder, tokenPort, initialBalance);
    }
}
