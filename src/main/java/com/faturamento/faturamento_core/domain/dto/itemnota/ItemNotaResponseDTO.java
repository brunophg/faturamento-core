package com.faturamento.faturamento_core.domain.dto.itemnota;

import com.faturamento.faturamento_core.domain.model.ItemNota;

import java.math.BigDecimal;

public record ItemNotaResponseDTO(
        Long id,
        Integer quantidade,
        BigDecimal valorUnitario
) {
    public static ItemNotaResponseDTO fromEntity(ItemNota item) {
        return new ItemNotaResponseDTO(
                item.getId(),
                item.getQuantidade(),
                item.getValorUnitario()
        );
    }
}
