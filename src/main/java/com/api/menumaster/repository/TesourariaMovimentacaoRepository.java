package com.api.menumaster.repository;

import com.api.menumaster.model.TesourariaMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TesourariaMovimentacaoRepository extends JpaRepository<TesourariaMovimentacao, UUID> {
}
