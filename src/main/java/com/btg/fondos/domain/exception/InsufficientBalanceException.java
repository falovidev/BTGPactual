package com.btg.fondos.domain.exception;

public class InsufficientBalanceException extends BusinessException {
    public InsufficientBalanceException(String fundName) {
        super("No tiene saldo disponible para vincularse al fondo " + fundName);
    }
}
