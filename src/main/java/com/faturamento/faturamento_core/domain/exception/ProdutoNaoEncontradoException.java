package com.faturamento.faturamento_core.domain.exception;

public class ProdutoNaoEncontradoException extends RegraNegocioException {
    public ProdutoNaoEncontradoException(String message) {
        super(message);
    }
}
