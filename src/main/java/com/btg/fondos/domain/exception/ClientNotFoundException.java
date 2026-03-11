package com.btg.fondos.domain.exception;

public class ClientNotFoundException extends BusinessException {
    public ClientNotFoundException(String clientId) {
        super("Cliente no encontrado con ID: " + clientId);
    }
}
