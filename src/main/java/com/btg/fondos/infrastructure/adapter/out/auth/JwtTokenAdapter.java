package com.btg.fondos.infrastructure.adapter.out.auth;

import com.btg.fondos.domain.auth.port.TokenPort;
import com.btg.fondos.infrastructure.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenAdapter implements TokenPort {

    private final JwtProvider jwtProvider;

    @Override
    public String generateToken(String clientId, String email, String role) {
        return jwtProvider.generateToken(clientId, email, role);
    }
}
