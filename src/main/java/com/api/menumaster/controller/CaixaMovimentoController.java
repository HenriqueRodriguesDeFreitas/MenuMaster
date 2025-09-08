package com.api.menumaster.controller;

import com.api.menumaster.dtos.request.RequestMovimentoCaixaDto;
import com.api.menumaster.dtos.response.ResponseMovimentoCaixaDto;
import com.api.menumaster.service.CaixaMovimentoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static com.api.menumaster.controller.utils.AuxiliarRetornoAuthentication.getAuthentication;

@RestController
@RequestMapping("caixa-movimento")
public class CaixaMovimentoController {

    private final CaixaMovimentoService movimentoService;

    public CaixaMovimentoController(CaixaMovimentoService movimentoService) {
        this.movimentoService = movimentoService;
    }

    @PostMapping("/adicionarDinheiro")
    @PreAuthorize("hasAnyAuthority('ADMIN','CAIXA_MOVIMENTO_ENTRADA')")
    public ResponseEntity<ResponseMovimentoCaixaDto> adicionarDinheiro(@RequestBody @Valid RequestMovimentoCaixaDto dto){
        return ResponseEntity.ok(movimentoService.adicionarDinheiro(dto, getAuthentication()));
    }

    @PostMapping("/retirarDinheiro")
    @PreAuthorize("hasAnyAuthority('ADMIN','CAIXA_MOVIMENTO_SAIDA')")
    public ResponseEntity<ResponseMovimentoCaixaDto> retirarDinheiro(@RequestBody @Valid RequestMovimentoCaixaDto dto){
        return ResponseEntity.ok(movimentoService.retirarDinheiro(dto, getAuthentication()));
    }
}
