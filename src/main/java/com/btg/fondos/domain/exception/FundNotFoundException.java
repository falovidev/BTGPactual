package com.btg.fondos.domain.exception;

public class FundNotFoundException extends BusinessException {
    public FundNotFoundException(String fundId) {
        super("Fondo no encontrado con ID: " + fundId);
    }
}
