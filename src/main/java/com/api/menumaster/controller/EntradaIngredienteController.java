package com.api.menumaster.controller;

import com.api.menumaster.dtos.request.RequestEntradaNotaIngredienteDto;
import com.api.menumaster.service.EntradaIngredienteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("entrada-ingrediente")
public class EntradaIngredienteController {

    private final EntradaIngredienteService entradaService;

    public EntradaIngredienteController(EntradaIngredienteService entradaService) {
        this.entradaService = entradaService;
    }

    @PostMapping("/{idFornecedor}")
    public ResponseEntity<?> salvarEntrada(@PathVariable("idFornecedor") UUID idFornecedor,
                                           @RequestBody @Valid RequestEntradaNotaIngredienteDto dto) {
        return ResponseEntity.ok(entradaService.entrada(idFornecedor, dto));
    }
}
