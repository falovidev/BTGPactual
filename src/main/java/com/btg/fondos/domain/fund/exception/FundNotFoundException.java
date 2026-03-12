package com.btg.fondos.domain.fund.exception;

import com.btg.fondos.domain.exception.BusinessException;

public class FundNotFoundException extends BusinessException {
    public FundNotFoundException(String fundId) {
        super("Fondo no encontrado con ID: " + fundId);
    }
}
