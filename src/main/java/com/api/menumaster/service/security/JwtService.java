package com.api.menumaster.service.security;

import com.api.menumaster.model.RefreshToken;
import com.api.menumaster.repository.RefreshTokenRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private final JwtEncoder encoder;
    private final RefreshTokenRepository refreshTokenRepository;

    public JwtService(JwtEncoder encoder, RefreshTokenRepository refreshTokenRepository) {
        this.encoder = encoder;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public String gerarTokenAcesso(Authentication authentication) {
        Instant agora = Instant.now();
        long expiracao = 1800L;//30min

        String escopos = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        var propriedadesToken = JwtClaimsSet.builder()
                .issuer("menumaster")
                .issuedAt(agora)
                .expiresAt(agora.plusSeconds(expiracao))
                .subject(authentication.getName())
                .claim("authorities", escopos)
                .claim("token_type", "access")
                .id(UUID.randomUUID().toString())
                .build();

        return encoder.encode(JwtEncoderParameters.from(propriedadesToken)).getTokenValue();
    }

    public String gerarRefreshToken(String usuario) {
        Instant agora = Instant.now();
        long expiracao = 2700L;//45min

        var propriedadesToken = JwtClaimsSet.builder()
                .issuer("menumaster")
                .issuedAt(agora)
                .expiresAt(agora.plusSeconds(expiracao))
                .subject(usuario)
                .claim("token_type", "refresh")
                .id(UUID.randomUUID().toString())
                .build();

        String token =
                encoder.encode(JwtEncoderParameters.from(propriedadesToken)).getTokenValue();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setJwtId(propriedadesToken.getId());
        refreshToken.setUsuario(usuario);
        refreshToken.setExpiracao(agora.plusSeconds(expiracao));
        refreshToken.setRevogado(false);

        refreshTokenRepository.save(refreshToken);

        return token;
    }
}
