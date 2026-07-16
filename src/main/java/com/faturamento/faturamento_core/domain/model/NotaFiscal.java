package com.faturamento.faturamento_core.domain.model;

import com.faturamento.faturamento_core.domain.enums.StatusNota;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Table(name = "tb_nota_fiscal")
@Entity
public class NotaFiscal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O numero da nota é obrigatório")
    @Column(name = "numero_nota", nullable = false)
    private Long numeroNota;

    @NotNull(message = "A data de emissão da nota é obrigatória")
    @Column(name = "data_emissao", nullable = false)
    private LocalDateTime dataEmissao;

    @NotNull(message = "O valor total da nota é obrigatório")
    @Column(name = "valor_total", nullable = false)
    private BigDecimal valorTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusNota status;

    @NotNull(message = "A empresa emissora é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresaEmissora;

    // orphanRemoval = true: se um item for removido da lista, será excluído do banco.
    @OneToMany(mappedBy = "notaFiscal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemNota> itens = new ArrayList<>();

    public NotaFiscal() {
    }

    public NotaFiscal(Long numeroNota, LocalDateTime dataEmissao, BigDecimal valorTotal, StatusNota status, Empresa empresaEmissora) {
        this.numeroNota = numeroNota;
        this.dataEmissao = dataEmissao;
        this.valorTotal = valorTotal;
        this.status = status;
        this.empresaEmissora = empresaEmissora;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumeroNota() {
        return numeroNota;
    }

    public void setNumeroNota(Long numeroNota) {
        this.numeroNota = numeroNota;
    }

    public LocalDateTime getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(LocalDateTime dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public StatusNota getStatus() {
        return status;
    }

    public void setStatus(StatusNota status) {
        this.status = status;
    }

    public Empresa getEmpresaEmissora() {
        return empresaEmissora;
    }

    public void setEmpresaEmissora(Empresa empresaEmissora) {
        this.empresaEmissora = empresaEmissora;
    }


    // Retorna uma cópia imutável
    public List<ItemNota> getItens() {
        return List.copyOf(itens);
    }

    public void addItem(ItemNota item) {
        this.itens.add(item);
        item.setNotaFiscal(this);
    }

    public void removeItem(ItemNota item) {
        this.itens.remove(item);
        item.setNotaFiscal(null);
    }
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        NotaFiscal that = (NotaFiscal) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
