package com.faturamento.faturamento_core.domain.exception;

public class CnpjDuplicadoException extends RegraNegocioException {
    public CnpjDuplicadoException(String message) {
        super(message);
    }
}
