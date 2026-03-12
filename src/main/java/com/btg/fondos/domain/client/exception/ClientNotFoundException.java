package com.btg.fondos.domain.client.exception;

import com.btg.fondos.domain.exception.BusinessException;

public class ClientNotFoundException extends BusinessException {
    public ClientNotFoundException(String clientId) {
        super("Cliente no encontrado con ID: " + clientId);
    }
}
