package com.faturamento.faturamento_core.domain.dto.produto;

import com.faturamento.faturamento_core.domain.model.Produto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProdutoRequestDTO(

        @NotNull
        String codigo,

        @NotBlank(message = "O nome do produto é obrigatorio")
        String nome,

        String descricao,

        @NotNull
        BigDecimal preco
) {
    public Produto toEntity() {
        Produto produto = new Produto();
        produto.setCodigo(this.codigo);
        produto.setNome(this.nome);
        produto.setDescricao(this.descricao);
        produto.setPreco(this.preco);
        return produto;
    }
}
