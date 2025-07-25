package com.api.menumaster.controller;

import com.api.menumaster.dtos.request.RequestLogin;
import com.api.menumaster.service.AutenticacaoService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AutenticacaoController {

    private final AutenticacaoService autenticacaoService;

    public AutenticacaoController(AutenticacaoService autenticacaoService) {
        this.autenticacaoService = autenticacaoService;
    }

    @PostMapping("authenticate")
    public String autenticar(@RequestBody RequestLogin login){
        return autenticacaoService.autenticar(login.usuario(), login.senha());
    }
}
