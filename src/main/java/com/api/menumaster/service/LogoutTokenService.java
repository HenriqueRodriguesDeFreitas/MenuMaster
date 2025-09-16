package com.api.menumaster.service;

import com.api.menumaster.repository.RefreshTokenRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

@Service
public class LogoutTokenService {

    private final JwtDecoder jwtDecoder;
    private final RefreshTokenRepository refreshTokenRepository;

    public LogoutTokenService(JwtDecoder jwtDecoder, RefreshTokenRepository refreshTokenRepository) {
        this.jwtDecoder = jwtDecoder;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public void logout(String refreshToken) {
        Jwt jwt = jwtDecoder.decode(refreshToken);

        refreshTokenRepository.findByJwtAndRevogadoFalse(jwt.getId())
                .ifPresent(token -> {
                    token.setRevogado(true);
                    refreshTokenRepository.save(token);
                });
    }
}
