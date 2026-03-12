package com.btg.fondos.domain.fund.exception;

import com.btg.fondos.domain.exception.BusinessException;

public class SubscriptionNotFoundException extends BusinessException {
    public SubscriptionNotFoundException(String fundName) {
        super("No tiene suscripción activa al fondo " + fundName);
    }
}
