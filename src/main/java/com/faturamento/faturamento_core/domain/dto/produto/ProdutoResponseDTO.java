package com.faturamento.faturamento_core.domain.dto.produto;

import com.faturamento.faturamento_core.domain.model.Produto;

public record ProdutoResponseDTO(
        Long id,
        String codigo,
        String nome,
        String descricao,
        Double preco,
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
