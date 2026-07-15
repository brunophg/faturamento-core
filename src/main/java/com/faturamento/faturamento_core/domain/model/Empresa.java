    package com.faturamento.faturamento_core.domain.model;

    import jakarta.persistence.*;
    import jakarta.validation.constraints.NotBlank;
    import jakarta.validation.constraints.Pattern;

    import java.util.ArrayList;
    import java.util.List;
    import java.util.Objects;
    @Table(name = "tb_empresa")
    @Entity
    public class Empresa {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @NotBlank(message = "A razão social é obrigatória")
        @Column(name = "razao_social", nullable = false)
        private String razaoSocial;

        @NotBlank(message = "O CNPJ é obrigatório")
        @Pattern(regexp = "\\d{14}", message = "O CNPJ deve conter exatamente 14 dígitos numéricos")
        @Column(name = "cnpj", nullable = false, unique = true, length = 14)
        private String cnpj;

        @NotBlank(message = "A inscrição estadual é obrigatória")
        @Column(name = "inscricao_estadual", nullable = false)
        private String inscricaoEstadual;

        @OneToMany(mappedBy = "empresaEmissora")
        private List<NotaFiscal> notasEmitidas = new ArrayList<>();

        public Empresa() {

        }
        public Empresa(String razaoSocial, String cnpj, String inscricaoEstadual) {
            this.razaoSocial = razaoSocial;
            this.cnpj = cnpj;
            this.inscricaoEstadual = inscricaoEstadual;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getRazaoSocial() {
            return razaoSocial;
        }

        public void setRazaoSocial(String razaoSocial) {
            this.razaoSocial = razaoSocial;
        }

        public String getCnpj() {
            return cnpj;
        }

        public void setCnpj(String cnpj) {
            this.cnpj = cnpj;
        }

        public String getInscricaoEstadual() {
            return inscricaoEstadual;
        }

        public void setInscricaoEstadual(String inscricaoEstadual) {
            this.inscricaoEstadual = inscricaoEstadual;
        }

        public List<NotaFiscal> getNotasEmitidas() {
            return notasEmitidas;
        }

        public void setNotasEmitidas(List<NotaFiscal> notasEmitidas) {
            this.notasEmitidas = notasEmitidas;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Empresa empresa = (Empresa) o;
            return Objects.equals(id, empresa.id);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }
    }
