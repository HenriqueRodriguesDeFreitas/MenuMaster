package com.api.menumaster.controller;

import com.api.menumaster.dtos.request.RequestMovimentoTesourariaDto;
import com.api.menumaster.dtos.response.ResponseTesourariaMovimentoDto;
import com.api.menumaster.service.TesourariaMovimentoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tesouraria-movimentacao")
public class TesourariaMovimentoController {

    private final TesourariaMovimentoService movimentacaoService;

    public TesourariaMovimentoController(TesourariaMovimentoService movimentacaoService) {
        this.movimentacaoService = movimentacaoService;
    }

    @PostMapping("/entrada")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TESOURARIA_MOVIMENTO_ENTRADA')")
    public ResponseEntity<ResponseTesourariaMovimentoDto> efetuarMovimentoEntrada(@RequestBody
                                                                                  @Valid
                                                                                  RequestMovimentoTesourariaDto dto) {
        return ResponseEntity.ok(movimentacaoService.efetuarMovimentoEntrada(dto, getAuthentication()));
    }

    @PostMapping("/saida")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TESOURARIA_MOVIMENTO_SAIDA')")
    public ResponseEntity<ResponseTesourariaMovimentoDto> efetuarMovimentoSaida(@RequestBody @Valid
                                                                                RequestMovimentoTesourariaDto dto) {
     return ResponseEntity.ok(movimentacaoService.efetuarMovimentoSaida(dto, getAuthentication()));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TESOURARIA_MOVIMENTO_READ')")
    public ResponseEntity<List<ResponseTesourariaMovimentoDto>> buscarMovimentacoes(){
        return ResponseEntity.ok(movimentacaoService.buscarMovimentacoes());
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
