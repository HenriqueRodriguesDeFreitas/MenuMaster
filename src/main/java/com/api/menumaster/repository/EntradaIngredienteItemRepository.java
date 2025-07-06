package com.api.menumaster.repository;

import com.api.menumaster.model.EntradaIngredienteItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EntradaIngredienteItemRepository extends JpaRepository<EntradaIngredienteItem, UUID> {
}
