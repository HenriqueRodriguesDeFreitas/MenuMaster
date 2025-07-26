package com.api.menumaster.repository;

import com.api.menumaster.model.Tesouraria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TesourariaRepository extends JpaRepository<Tesouraria, UUID> {

    boolean existsByDataFechamentoIsNull();

    Optional<Tesouraria> findFirstByDataFechamentoIsNotNullOrderByDataFechamentoDesc();

    Optional<Tesouraria> findByDataFechamentoIsNull();

    Optional<Tesouraria> findByDataFechamento(LocalDateTime hoje);

    List<Tesouraria> findByDataFechamentoBetween(LocalDateTime inicio, LocalDateTime fim);

    List<Tesouraria> findByDataAberturaBetweenOrderByDataAbertura(LocalDateTime dataInicio, LocalDateTime dataFim);
}
