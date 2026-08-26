package com.faturamento.faturamento_core.domain.repository;

import com.faturamento.faturamento_core.domain.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    boolean existsByCodigo(String codigo);

    List<Produto> findAllByAtivoTrue();

    Optional<Produto> findByCodigo(String codigo);
}
