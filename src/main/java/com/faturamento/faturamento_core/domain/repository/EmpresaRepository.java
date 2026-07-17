package com.faturamento.faturamento_core.domain.repository;

import com.faturamento.faturamento_core.domain.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
}
