package com.api.menumaster.controller;

import com.api.menumaster.dtos.response.ResponseTesourariaDto;
import com.api.menumaster.service.TesourariaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("tesouraria")
public class TesourariaController {

    private final TesourariaService tesourariaService;

    public TesourariaController(TesourariaService tesourariaService) {
        this.tesourariaService = tesourariaService;
    }

    @PostMapping("/abrir")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TESOURARIA_ABRIR')")
    public ResponseEntity<ResponseTesourariaDto> abrirTesouraria() {
        return ResponseEntity.ok(tesourariaService.abrirTesouraria(getAuthentication()));
    }

    @PostMapping("/fechar")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TESOURARIA_FECHAR')")
    public ResponseEntity<ResponseTesourariaDto> fecharTesouraria() {
        return ResponseEntity.ok(tesourariaService.fecharTesouraria(getAuthentication()));
    }

    @PostMapping("/reabrir")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TESOURARIA_ABRIR')")
    public ResponseEntity<ResponseTesourariaDto> reabrirTesouraria() {
        return ResponseEntity.ok(tesourariaService.reabrirTesouraria(getAuthentication()));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TESOURARIA_READ')")
    public ResponseEntity<List<ResponseTesourariaDto>> buscarTodasTesourarias() {
        return ResponseEntity.ok(tesourariaService.buscarTodasTesourarias());
    }

    @GetMapping("/byDataAbertura")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TESOURARIA_READ')")
    public ResponseEntity<List<?>> buscarPorDataAbertura(
            @RequestParam(value = "dataInicial", required = true) LocalDate dataInicio,
            @RequestParam(value = "dataFinal", required = false) LocalDate dataFinal) {
        return ResponseEntity.ok(tesourariaService.buscarTesourariasPorDataAbertura(dataInicio, dataFinal));
    }


    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
