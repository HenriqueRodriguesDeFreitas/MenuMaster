package com.api.menumaster.controller;

import com.api.menumaster.service.TesourariaService;
import jakarta.websocket.server.PathParam;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("tesouraria")
public class TesourariaController {

    private final TesourariaService tesourariaService;

    public TesourariaController(TesourariaService tesourariaService) {
        this.tesourariaService = tesourariaService;
    }

    @PostMapping
    public ResponseEntity<?> abrirTesouraria(@RequestParam("abrir") boolean abrir) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(tesourariaService.abrirTesouraria(abrir, authentication));
    }
}
