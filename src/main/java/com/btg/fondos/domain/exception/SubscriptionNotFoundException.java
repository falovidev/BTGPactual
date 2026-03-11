package com.btg.fondos.domain.exception;

public class SubscriptionNotFoundException extends BusinessException {
    public SubscriptionNotFoundException(String fundName) {
        super("No tiene suscripción activa al fondo " + fundName);
    }
}
