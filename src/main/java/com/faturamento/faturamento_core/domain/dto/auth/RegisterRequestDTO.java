package com.faturamento.faturamento_core.domain.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDTO(
        @NotBlank(message = "O login é obrigatório")
        String login,
        @NotBlank(message = "A senha é obrigatoria")
        String senha
) {
}
