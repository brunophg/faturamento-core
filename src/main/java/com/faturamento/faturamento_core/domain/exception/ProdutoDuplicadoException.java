package com.faturamento.faturamento_core.domain.exception;

public class ProdutoDuplicadoException extends RuntimeException {
    public ProdutoDuplicadoException(String message) {
        super(message);
    }
}
