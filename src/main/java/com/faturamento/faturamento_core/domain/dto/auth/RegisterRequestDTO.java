package com.faturamento.faturamento_core.domain.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDTO(
        @NotBlank String login,
        @NotBlank String senha
) {
}
