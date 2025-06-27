package com.api.menumaster.repository;

import com.api.menumaster.model.Fornecedor;
import org.apache.catalina.LifecycleState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FornecedorRepository extends JpaRepository<Fornecedor, UUID> {

    Optional<Fornecedor> findByRazaoSocialIgnoreCase(String razaoSocial);
    List<Fornecedor> findByRazaoSocialContainingIgnoreCase(String razaoSocial);

    Optional<Fornecedor> findByNomeFantasiaIgnoreCase(String nomeFantasia);
    List<Fornecedor> findByNomeFantasiaContainingIgnoreCase(String nomeFantasia);

    Optional<Fornecedor> findByCnpj(String cnpj);
    Optional<Fornecedor> findByInscricaoEstadual(String inscricaoEstadual);

    List<Fornecedor> findByIsAtivoTrue();
}
