package com.faturamento.faturamento_core.domain.repository;

import com.faturamento.faturamento_core.domain.model.NotaFiscal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotaFiscalRepository extends JpaRepository<NotaFiscal, Long> {

    // RN1 Evitar duplicidade.
    boolean existsByNumeroNotaAndEmpresaEmissoraId(Long numeroNota, Long empresaId);

    Page<NotaFiscal> findByEmpresaEmissoraId(Pageable pageable, Long empresaId);
}
