package com.faturamento.faturamento_core.domain.enums;

public enum StatusNota {
    PROCESSANDO(1, "Nota em processamento"),
    EMITIDA(2, "Nota emitida com sucesso"),
    CANCELADA(3, "Nota cancelada");

    private int codigo;
    private final String descricao;

    private StatusNota(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public int getCodigo() {
        return codigo;
    }

    public static StatusNota valueOf(int codigo) {
        for (StatusNota value : StatusNota.values()) {
            if (value.getCodigo() == codigo) {
                return value;
            }
        }
        throw new IllegalArgumentException("Codigo StatusNota invalido");
    }
}