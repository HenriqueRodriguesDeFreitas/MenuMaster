package com.api.menumaster.repository;

import com.api.menumaster.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByNome(String nome);
    List<Usuario> findByNomeContainingOrderByNome(String nome);
}
