package com.faturamento.faturamento_core.domain.repository;

import com.faturamento.faturamento_core.domain.model.Empresa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    boolean existsByCnpj(String cnpj);

    Page<Empresa> findAllByAtivoTrue(Pageable pageable);

    Optional<Empresa> findByIdAndAtivoTrue(Long id);

    Optional<Empresa> findByCnpjAndAtivoTrue(String cnpj);
}
