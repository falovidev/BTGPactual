package com.btg.fondos.domain.fund.exception;

import com.btg.fondos.domain.exception.BusinessException;

public class DuplicateSubscriptionException extends BusinessException {
    public DuplicateSubscriptionException(String fundName) {
        super("Ya se encuentra suscrito al fondo " + fundName);
    }
}
