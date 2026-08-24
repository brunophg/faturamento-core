package com.faturamento.faturamento_core.domain.dto.produto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProdutoUpdateDTO (
        @NotBlank(message = "O nome não pode estar em branco")
        String nome,

        @NotBlank(message = "A descrição não pode estar em branco")
        String descricao,

        @NotNull(message = "O preço é obrigatório")
        @Positive(message = "O preço deve ser maior que zero")
        Double preco
) {
}
