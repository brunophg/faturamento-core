package com.faturamento.faturamento_core.domain.dto.empresa;

import com.faturamento.faturamento_core.domain.model.Empresa;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EmpresaRequestDTO(
        @NotBlank(message = "A razão social é obrigatória")
        String razaoSocial,

        @NotBlank(message = "O CNPJ é obrigatório")
        @Pattern(regexp = "^\\d{14}$", message = "O CNPJ deve conter exatamente 14 dígitos numéricos.")
        String cnpj,

        @NotBlank(message = "A inscrição estadual é obrigatória.")
        String inscricaoEstadual
) {
    public Empresa toEntity() {
        Empresa emp = new Empresa();
        emp.setRazaoSocial(this.razaoSocial);
        emp.setCnpj(this.cnpj);
        emp.setInscricaoEstadual(this.inscricaoEstadual);
        return emp;
    }
}
