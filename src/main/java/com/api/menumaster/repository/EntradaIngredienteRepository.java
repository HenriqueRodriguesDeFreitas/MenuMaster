package com.api.menumaster.repository;

import com.api.menumaster.model.EntradaIngrediente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EntradaIngredienteRepository extends JpaRepository<EntradaIngrediente, UUID> {

    Optional<EntradaIngrediente> findByNumeroNota(String numeroNota);

    List<EntradaIngrediente> findByDataEntrada(LocalDate dataEntrada);

    List<EntradaIngrediente> findByFornecedorRazaoSocial(String razaoSocial);

    List<EntradaIngrediente> findByFornecedorNomeFantasia(String nomeFantasia);

    List<EntradaIngrediente> findByValorTotal(BigDecimal valorTotal);

    boolean existsByFornecedorIdAndNumeroNota(UUID id, String numeroNota);
}
