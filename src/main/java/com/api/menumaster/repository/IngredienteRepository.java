package com.api.menumaster.repository;

import com.api.menumaster.model.Ingrediente;
import com.api.menumaster.model.enums.UnidadeMedida;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IngredienteRepository extends JpaRepository<Ingrediente, UUID> {

    Optional<Ingrediente> findByCodigo(Integer codigo);

    Optional<Ingrediente> findByNomeIgnoreCase(String nome);
    List<Ingrediente> findByNomeContainingIgnoreCase(String nome);

    List<Ingrediente> findByDescricaoContainingIgnoreCase(String descricao);

    List<Ingrediente> findByPrecoCusto(BigDecimal precoCusto);
    List<Ingrediente> findByPrecoVenda(BigDecimal precoVenda);

    List<Ingrediente> findByIsAtivoTrue();
    List<Ingrediente> findByIsAtivoFalse();

    List<Ingrediente> findByIsAdicionalTrue();
    List<Ingrediente> findByIsAdicionalFalse();

    List<Ingrediente> findByUnidadeMedida(UnidadeMedida unidadeMedida);

    List<Ingrediente> findByControlarEstoqueTrue();
    List<Ingrediente> findByControlarEstoqueFalse();

}
