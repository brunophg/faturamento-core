package com.faturamento.faturamento_core.domain.exception;

public abstract class RegraNegocioException extends RuntimeException {
    public RegraNegocioException(String message) {
        super(message);
    }
}
