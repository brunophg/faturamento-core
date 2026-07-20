package com.faturamento.faturamento_core.domain.exception;

public class EmpresaNaoEncontradaException extends RegraNegocioException {
    public EmpresaNaoEncontradaException(String message) {
        super(message);
    }
}
