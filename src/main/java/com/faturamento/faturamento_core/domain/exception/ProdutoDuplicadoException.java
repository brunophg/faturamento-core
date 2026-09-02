package com.faturamento.faturamento_core.domain.exception;

public class ProdutoDuplicadoException extends RegraNegocioException {
    public ProdutoDuplicadoException(String message) {
        super(message);
    }
}
