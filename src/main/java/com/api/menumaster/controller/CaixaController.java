package com.api.menumaster.controller;

import com.api.menumaster.dtos.request.RequestAbrirCaixaDto;
import com.api.menumaster.dtos.response.ResponseAbrirCaixaDto;
import com.api.menumaster.dtos.response.ResponseCaixaDto;
import com.api.menumaster.service.CaixaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.api.menumaster.controller.utils.AuxiliarRetornoAuthentication.getAuthentication;

@RestController
@RequestMapping("caixa")
public class CaixaController {

    private final CaixaService caixaService;

    public CaixaController(CaixaService caixaService) {
        this.caixaService = caixaService;
    }

    @PostMapping("/abrir")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CAIXA_OPERADOR')")
    public ResponseEntity<ResponseAbrirCaixaDto> abrirCaixa(@RequestBody @Valid RequestAbrirCaixaDto dto) {
        return ResponseEntity.ok(caixaService.abrirCaixa(dto, getAuthentication()));
    }

    @PostMapping("/fechar")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CAIXA_FECHAR')")
    public ResponseEntity<ResponseCaixaDto> fecharCaixa() {
        return ResponseEntity.ok(caixaService.fecharCaixa(getAuthentication()));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CAIXA_BUSCAR')")
    public ResponseEntity<List<ResponseCaixaDto>> buscarCaixas() {
        return ResponseEntity.ok(caixaService.buscarCaixas());
    }
}
