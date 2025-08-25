package com.api.menumaster.repository;

import com.api.menumaster.model.TesourariaMovimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TesourariaMovimentoRepository extends JpaRepository<TesourariaMovimento, UUID> {
}
