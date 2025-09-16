package com.api.menumaster.controller;

import com.api.menumaster.dtos.request.RequestLogin;
import com.api.menumaster.dtos.request.RequestLogoutTokenDto;
import com.api.menumaster.dtos.request.RequestRefreshToken;
import com.api.menumaster.dtos.response.ResponseToken;
import com.api.menumaster.service.AutenticacaoService;
import com.api.menumaster.service.LogoutTokenService;
import com.api.menumaster.service.RefreshTokenService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("autenticacao")
public class AutenticacaoController {

    private final AutenticacaoService autenticacaoService;
    private final RefreshTokenService refreshTokenService;
    private final LogoutTokenService logoutTokenService;

    public AutenticacaoController(AutenticacaoService autenticacaoService,
                                  RefreshTokenService refreshTokenService,
                                  LogoutTokenService logoutTokenService) {
        this.autenticacaoService = autenticacaoService;
        this.refreshTokenService = refreshTokenService;
        this.logoutTokenService = logoutTokenService;
    }

    @PostMapping("/autenticar")
    public ResponseToken autenticar(@RequestBody RequestLogin login) {
        return autenticacaoService.autenticar(login.usuario(), login.senha());
    }

    @PostMapping("/refresh")
    public ResponseToken refresh(@RequestBody RequestRefreshToken token) {
        return refreshTokenService.refreshAccessToken(token.refreshToken());
    }

    @PostMapping("/logout")
    public void logout(@RequestBody RequestLogoutTokenDto token) {
        logoutTokenService.logout(token.token());
    }
}
