package com.api.menumaster.repository;

import com.api.menumaster.model.CaixaMovimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CaixaMovimentoRepository extends JpaRepository<CaixaMovimento, UUID> {
}
