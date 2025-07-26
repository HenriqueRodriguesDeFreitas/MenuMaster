package com.api.menumaster.controller;

import com.api.menumaster.dtos.response.ResponseTesourariaDto;
import com.api.menumaster.service.TesourariaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("tesouraria")
public class TesourariaController {

    private final TesourariaService tesourariaService;

    public TesourariaController(TesourariaService tesourariaService) {
        this.tesourariaService = tesourariaService;
    }

    @PostMapping("/abrir")
    public ResponseEntity<ResponseTesourariaDto> abrirTesouraria() {
        return ResponseEntity.ok(tesourariaService.abrirTesouraria(getAuthentication()));
    }

    @PostMapping("/fechar")
    public ResponseEntity<ResponseTesourariaDto> fecharTesouraria(){
        return ResponseEntity.ok(tesourariaService.fecharTesouraria(getAuthentication()));
    }

    @PostMapping("/reabrir")
    public ResponseEntity<ResponseTesourariaDto> reabrirTesouraria(){
        return ResponseEntity.ok(tesourariaService.reabrirTesouraria(getAuthentication()));
    }

    private Authentication getAuthentication(){
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
