package com.api.menumaster.controller;

import com.api.menumaster.dtos.request.RequestUsuarioDto;
import com.api.menumaster.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'LIDER')")
    public ResponseEntity<?> salvar(@RequestBody @Valid RequestUsuarioDto dto){
        return ResponseEntity.ok(usuarioService.salvar(dto));
    }
}
