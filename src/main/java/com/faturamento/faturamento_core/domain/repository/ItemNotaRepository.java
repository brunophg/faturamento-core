package com.faturamento.faturamento_core.domain.repository;

import com.faturamento.faturamento_core.domain.model.ItemNota;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemNotaRepository extends JpaRepository<ItemNota, Long> {
}
