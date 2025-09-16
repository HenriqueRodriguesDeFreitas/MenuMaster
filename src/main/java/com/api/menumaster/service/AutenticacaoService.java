package com.api.menumaster.service;

import com.api.menumaster.dtos.response.ResponseToken;
import com.api.menumaster.service.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AutenticacaoService {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AutenticacaoService(JwtService jwtService, AuthenticationManager authenticationManager) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public ResponseToken autenticar(String usuario, String senha) {
        var auth = new UsernamePasswordAuthenticationToken(usuario, senha);
        Authentication authentication = authenticationManager.authenticate(auth);

        String access = jwtService.gerarTokenAcesso(auth);
        String refresh = jwtService.gerarRefreshToken(auth.getName());

        return new ResponseToken(access, refresh);
    }
}
