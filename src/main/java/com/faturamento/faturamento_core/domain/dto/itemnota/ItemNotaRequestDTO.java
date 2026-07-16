package com.faturamento.faturamento_core.domain.dto.itemnota;

import com.faturamento.faturamento_core.domain.model.ItemNota;

import java.math.BigDecimal;

public record ItemNotaRequestDTO(
        String descricao,
        Integer quantidade,
        BigDecimal valorUnitario
) {
    public ItemNota toEntity() {
        ItemNota item = new ItemNota();
        item.setDescricao(this.descricao);
        item.setQuantidade(this.quantidade);
        item.setValorUnitario(this.valorUnitario);
        return item;
    }
}
