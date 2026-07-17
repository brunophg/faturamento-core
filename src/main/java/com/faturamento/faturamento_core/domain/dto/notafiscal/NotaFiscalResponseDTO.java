package com.faturamento.faturamento_core.domain.dto.notafiscal;

import com.faturamento.faturamento_core.domain.dto.itemnota.ItemNotaResponseDTO;
import com.faturamento.faturamento_core.domain.model.NotaFiscal;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record NotaFiscalResponseDTO(
        Long id,
        Long numeroNota,
        LocalDateTime dataEmissao,
        BigDecimal valorTotal,
        String status,
        Long empresaId,
        List<ItemNotaResponseDTO> itens
) {
    public static NotaFiscalResponseDTO fromEntity(NotaFiscal nota) {
        return new NotaFiscalResponseDTO(
                nota.getId(),
                nota.getNumeroNota(),
                nota.getDataEmissao(),
                nota.getValorTotal(),
                nota.getStatus().name(),
                nota.getEmpresaEmissora().getId(),
                nota.getItens().stream()
                        .map(ItemNotaResponseDTO::fromEntity)
                        .toList()
        );
    }
}
