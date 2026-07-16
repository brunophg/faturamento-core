package com.faturamento.faturamento_core.domain.dto.notafiscal;

import com.faturamento.faturamento_core.domain.dto.itemnota.ItemNotaRequestDTO;
import com.faturamento.faturamento_core.domain.enums.StatusNota;
import com.faturamento.faturamento_core.domain.model.Empresa;
import com.faturamento.faturamento_core.domain.model.NotaFiscal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record NotaFiscalRequestDTO(
        @NotNull(message = "O número da nota é obrigatório.")
        Long numeroNota,

        @NotNull(message = "A data de emissão da nota é obrigatória.")
        LocalDateTime dataEmissao,

        @NotNull(message = "O id da empresa emissora é obrigatório.")
        Long empresaId,

        @NotEmpty(message = "A nota fiscal deve conter pelo menos um item.")
        @Valid
        List <ItemNotaRequestDTO> itens
) {
    public NotaFiscal toEntity() {
        NotaFiscal nota = new NotaFiscal();
        nota.setNumeroNota(this.numeroNota);
        nota.setDataEmissao(this.dataEmissao);

        // Toda nota nova nasce com o status de processamento
        nota.setStatus(StatusNota.PROCESSANDO);

        return nota;
    }
}
