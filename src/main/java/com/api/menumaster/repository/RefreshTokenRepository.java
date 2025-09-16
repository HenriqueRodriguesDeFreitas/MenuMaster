package com.api.menumaster.repository;

import com.api.menumaster.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByJwtIdAndRevogadoFalse(String jwtId);
}
