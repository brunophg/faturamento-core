package com.faturamento.faturamento_core.domain.exception;

public class CnpjInvalidoException extends RegraNegocioException {
    public CnpjInvalidoException(String message) {
        super(message);
    }
}
