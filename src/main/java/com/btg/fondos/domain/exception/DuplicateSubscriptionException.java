package com.btg.fondos.domain.exception;

public class DuplicateSubscriptionException extends BusinessException {
    public DuplicateSubscriptionException(String fundName) {
        super("Ya se encuentra suscrito al fondo " + fundName);
    }
}
