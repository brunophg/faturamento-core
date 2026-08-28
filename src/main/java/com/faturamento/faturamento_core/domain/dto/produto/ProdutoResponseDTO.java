package com.faturamento.faturamento_core.domain.dto.produto;

import com.faturamento.faturamento_core.domain.model.Produto;

import java.math.BigDecimal;

public record ProdutoResponseDTO(
        Long id,
        String codigo,
        String nome,
        String descricao,
        BigDecimal preco,
        Boolean ativo
) {
    public static ProdutoResponseDTO fromEntity(Produto produto) {
        return new ProdutoResponseDTO(
                produto.getId(),
                produto.getCodigo(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getAtivo()
        );
    }
}
