package com.api.menumaster.repository;

import com.api.menumaster.model.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuthorityRepository extends JpaRepository<Authority, UUID> {
    Optional<Authority> findByNome(String nome);
}
