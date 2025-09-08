package com.api.menumaster.repository;

import com.api.menumaster.model.Caixa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CaixaRepository extends JpaRepository<Caixa, UUID > {
    Optional<Caixa> findByUsuarioUtilizandoAndDataFechamentoIsNull(String usuario);
}
