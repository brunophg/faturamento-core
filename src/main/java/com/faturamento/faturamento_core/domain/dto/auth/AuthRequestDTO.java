package com.faturamento.faturamento_core.domain.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record AuthRequestDTO(
        @NotBlank(message = "O login não pode estar vazio")
        String login,

        @NotBlank(message = "A senha não pode estar vazia")
        String senha
) {

}
