package com.api.menumaster.controller;

import com.api.menumaster.dtos.request.RequestEntradaNotaIngredienteDto;
import com.api.menumaster.dtos.request.RequestUpdateItensEntradaIngredienteDto;
import com.api.menumaster.service.EntradaIngredienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("entrada-ingrediente")
public class EntradaIngredienteController {

    private final EntradaIngredienteService entradaService;

    public EntradaIngredienteController(EntradaIngredienteService entradaService) {
        this.entradaService = entradaService;
    }

    @PostMapping("/{idFornecedor}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'INGREDIENTE_ENTRADA_CREATE')")
    public ResponseEntity<?> salvarEntrada(@PathVariable("idFornecedor") UUID idFornecedor,
                                           @RequestBody @Valid RequestEntradaNotaIngredienteDto dto) {
        return ResponseEntity.ok(entradaService.salvarEntradaNota(idFornecedor, dto));
    }

    @PutMapping("/{numeroNota}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'INGREDIENTE_ENTRADA_UPDATE')")
    public ResponseEntity<?> atualizarEntrada(@PathVariable("numeroNota") String numeroNota,
                                              @RequestBody RequestUpdateItensEntradaIngredienteDto dto) {
        return ResponseEntity.ok(entradaService.atualizarItensDaNota(numeroNota, dto));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'INGREDIENTE_ENTRADA_READ')")
    public ResponseEntity<List<?>> findAll() {
        return ResponseEntity.ok(entradaService.buscarEntradasIngrediente());
    }

    @GetMapping("/byRazaoSocial/{razaoSocial}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'INGREDIENTE_ENTRADA_READ')")
    public ResponseEntity<List<?>> findByFornecedorRazaoSocial(@PathVariable("razaoSocial") String razaoSocial) {
        return ResponseEntity.ok(entradaService.buscarEntradaIngredientePorFornecedorRazaoSocial(razaoSocial));
    }

    @GetMapping("/byNomeFantasia/{nomeFantasia}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'INGREDIENTE_ENTRADA_READ')")
    public ResponseEntity<List<?>> findByFornecedorNomeFantasia(
            @PathVariable("nomeFantasia") String nomeFantasia) {
        return ResponseEntity.ok(entradaService.buscarEntradaIngredientePorFornecedorNomeFantasia(nomeFantasia));
    }

    @GetMapping("/byDataEntrada/{dataEntrada}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'INGREDIENTE_ENTRADA_READ')")
    public ResponseEntity<List<?>> findByDataEntrada(
            @PathVariable("dataEntrada") LocalDate dataEntrada) {
        return ResponseEntity.ok(entradaService.buscarEntradaIngredientePorDataEntrada(dataEntrada));
    }

    @GetMapping("/byNumeroNota/{numeroNota}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'INGREDIENTE_ENTRADA_READ')")
    public ResponseEntity<?> findByDataEntrada(
            @PathVariable("numeroNota") String numeroNota) {
        return ResponseEntity.ok(entradaService.buscarEntradaIngredientePorNumeroNota(numeroNota));
    }

    @GetMapping("/byValorTotal/{valorTotal}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'INGREDIENTE_ENTRADA_READ')")
    public ResponseEntity<?> findByValorTotal(
            @PathVariable("valorTotal") BigDecimal valorTotal) {
        return ResponseEntity.ok(entradaService.buscarEntradaIngredientePorValorTotalNota(valorTotal));
    }

    @DeleteMapping("/byNumeroNota/{numeroNota}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'INGREDIENTE_ENTRADA_DELETE')")
    public ResponseEntity<?> deleteByNumeroNota(@PathVariable("numeroNota") String numeroNota) {
        entradaService.deleteByNumeroNota(numeroNota);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
