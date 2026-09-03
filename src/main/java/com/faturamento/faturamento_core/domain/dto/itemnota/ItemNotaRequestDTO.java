package com.faturamento.faturamento_core.domain.dto.itemnota;

import com.faturamento.faturamento_core.domain.model.ItemNota;

import java.math.BigDecimal;

public record ItemNotaRequestDTO(
        Long produtoId,
        Integer quantidade,
        BigDecimal valorUnitario
) {}
