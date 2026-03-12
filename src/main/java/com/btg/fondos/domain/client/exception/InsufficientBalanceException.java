package com.btg.fondos.domain.client.exception;

import com.btg.fondos.domain.exception.BusinessException;

public class InsufficientBalanceException extends BusinessException {
    public InsufficientBalanceException(String fundName) {
        super("No tiene saldo disponible para vincularse al fondo " + fundName);
    }
}
