package com.api.menumaster.repository;

import com.api.menumaster.model.IngredienteProduto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IngredienteProdutoRepository extends JpaRepository<IngredienteProduto, UUID> {
}
