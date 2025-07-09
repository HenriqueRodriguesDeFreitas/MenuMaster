package com.api.menumaster.repository;

import com.api.menumaster.model.EntradaIngrediente;
import com.api.menumaster.model.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.ListIterator;
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
