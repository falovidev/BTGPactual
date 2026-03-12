package com.btg.fondos.domain.auth.port;

public interface TokenPort {
    String generateToken(String clientId, String email, String role);
}
