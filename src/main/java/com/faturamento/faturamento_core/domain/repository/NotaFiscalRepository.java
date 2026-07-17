package com.faturamento.faturamento_core.domain.repository;

import com.faturamento.faturamento_core.domain.model.NotaFiscal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotaFiscalRepository extends JpaRepository<NotaFiscal, Long> {
}
