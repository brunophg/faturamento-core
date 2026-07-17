package com.faturamento.faturamento_core.domain.dto.empresa;

import com.faturamento.faturamento_core.domain.model.Empresa;

public record EmpresaResponseDTO(
        Long id,
        String razaoSocial,
        String cnpj,
        String inscriçãoEstadual
) {
    public static EmpresaResponseDTO fromEntity(Empresa emp) {
        return new EmpresaResponseDTO(
                emp.getId(),
                emp.getRazaoSocial(),
                emp.getCnpj(),
                emp.getInscricaoEstadual()
        );
    }
}
