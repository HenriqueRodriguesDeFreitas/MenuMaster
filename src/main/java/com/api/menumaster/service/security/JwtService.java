package com.api.menumaster.service.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private final JwtEncoder encoder;

    public JwtService(JwtEncoder encoder) {
        this.encoder = encoder;
    }

    public String generateToken(Authentication authentication) {
        Instant agora = Instant.now();
        long expiracao = 3600L;

        String escopos = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        var propriedadesToken = JwtClaimsSet.builder()
                .issuer("menumaster")
                .issuedAt(agora)
                .expiresAt(agora.plusSeconds(expiracao))
                .subject(authentication.getName())
                .claim("authorities", escopos)
                .build();

        return encoder.encode(JwtEncoderParameters.from(propriedadesToken)).getTokenValue();
    }
}
