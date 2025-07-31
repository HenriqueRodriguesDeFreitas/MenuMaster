package com.api.menumaster.controller;

import com.api.menumaster.dtos.request.RequestMovimentoTesourariaDto;
import com.api.menumaster.dtos.response.ResponseMovimentoTesourariaDto;
import com.api.menumaster.service.TesourariaMovimentacaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tesouraria-movimentacao")
public class TesourariaMovimentacaoController {

    private final TesourariaMovimentacaoService movimentacaoService;

    public TesourariaMovimentacaoController(TesourariaMovimentacaoService movimentacaoService) {
        this.movimentacaoService = movimentacaoService;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ResponseMovimentoTesourariaDto> efetuarMovimento(@RequestBody
                                                                           @Valid
                                                                           RequestMovimentoTesourariaDto dto){
    return ResponseEntity.ok(movimentacaoService.efetuarMovimento(dto, getAuthentication()));
    }

    private Authentication getAuthentication(){
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
