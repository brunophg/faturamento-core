package com.faturamento.faturamento_core.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "tb_item_nota")
public class ItemNota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "A quantidade do item não pode estar vazia.")
    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @NotNull(message = "O valor unitário é obrigatório.")
    @Column(name = "valor_unitario", nullable = false)
    private BigDecimal valorUnitario;

    // Lazy Evita carregar a NotaFiscal inteira ao buscar apenas um item.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nota_fiscal_id", nullable = false)
    private NotaFiscal notaFiscal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    public ItemNota() {

    }

    public ItemNota(String descricao, Integer quantidade, BigDecimal valorUnitario, NotaFiscal notaFiscal, Produto produto) {
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
        this.notaFiscal = notaFiscal;
        this.produto = produto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public NotaFiscal getNotaFiscal() {
        return notaFiscal;
    }

    public void setNotaFiscal(NotaFiscal notaFiscal) {
        this.notaFiscal = notaFiscal;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemNota itemNota = (ItemNota) o;
        return Objects.equals(id, itemNota.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

