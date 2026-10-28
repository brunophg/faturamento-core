package com.faturamento.faturamento_core.domain.exception;

public class LoginDuplicadoException extends RegraNegocioException {
    public LoginDuplicadoException(String message) {
        super(message);
    }
}
