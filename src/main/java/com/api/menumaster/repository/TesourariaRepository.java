package com.api.menumaster.repository;

import com.api.menumaster.model.Tesouraria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TesourariaRepository extends JpaRepository<Tesouraria, UUID> {

  boolean existsByDataFechamentoIsNull();
  Optional<Tesouraria> findFirstByDataFechamentoIsNotNullOrderByDataFechamentoDesc();
}
