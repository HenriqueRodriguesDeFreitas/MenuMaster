package com.api.menumaster.service;

import com.api.menumaster.dtos.response.ResponseToken;
import com.api.menumaster.model.RefreshToken;
import com.api.menumaster.repository.RefreshTokenRepository;
import com.api.menumaster.service.security.CustomUserDetailsService;
import com.api.menumaster.service.security.JwtService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService {

    private final JwtService jwtService;
    private final JwtDecoder jwtDecoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CustomUserDetailsService userDetailsService;

    public RefreshTokenService(JwtService jwtService, JwtDecoder jwtDecoder, RefreshTokenRepository refreshTokenRepository, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.jwtDecoder = jwtDecoder;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userDetailsService = userDetailsService;
    }

    public ResponseToken refreshAccessToken(String refreshToken) {
        Jwt jwt = jwtDecoder.decode(refreshToken);

        if (!"refresh".equals(jwt.getClaimAsString("token_type"))) {
            throw new RuntimeException("Token invalido.");
        }

        RefreshToken refresh = refreshTokenRepository
                .findByJwtIdAndRevogadoFalse(jwt.getId())
                .orElseThrow(() -> new RuntimeException("Refresh token inválido ou revogado."));

        refresh.setRevogado(true);
        refreshTokenRepository.save(refresh);

        var userDetails = userDetailsService.loadUserByUsername(refresh.getUsuario());
        Authentication auth = new UsernamePasswordAuthenticationToken(
                refresh.getUsuario(),
                null,
                userDetails.getAuthorities());

        String novoAccess = jwtService.gerarTokenAcesso(auth);
        String novoRefresh = jwtService.gerarRefreshToken(refresh.getUsuario());

        return new ResponseToken(novoAccess, novoRefresh);
    }
}
