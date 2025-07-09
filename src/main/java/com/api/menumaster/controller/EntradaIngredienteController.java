package com.api.menumaster.controller;

import com.api.menumaster.dtos.request.RequestEntradaNotaIngredienteDto;
import com.api.menumaster.dtos.request.RequestUpdateItensEntradaIngredienteDto;
import com.api.menumaster.service.EntradaIngredienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> salvarEntrada(@PathVariable("idFornecedor") UUID idFornecedor,
                                           @RequestBody @Valid RequestEntradaNotaIngredienteDto dto) {
        return ResponseEntity.ok(entradaService.entrada(idFornecedor, dto));
    }

    @PutMapping("/{numeroNota}")
    public ResponseEntity<?> atualizarEntrada(@PathVariable("numeroNota") String numeroNota,
                                              @RequestBody RequestUpdateItensEntradaIngredienteDto dto) {
        return ResponseEntity.ok(entradaService.atualizarItensDaNota(numeroNota, dto));
    }

    @GetMapping
    public ResponseEntity<List<?>> findAll() {
        return ResponseEntity.ok(entradaService.findAll());
    }

    @GetMapping("/byRazaoSocial/{razaoSocial}")
    public ResponseEntity<List<?>> findByFornecedorRazaoSocial(@PathVariable("razaoSocial") String razaoSocial) {
        return ResponseEntity.ok(entradaService.findByFornecedorRazaoSocial(razaoSocial));
    }

    @GetMapping("/byNomeFantasia/{nomeFantasia}")
    public ResponseEntity<List<?>> findByFornecedorNomeFantasia(
            @PathVariable("nomeFantasia") String nomeFantasia) {
        return ResponseEntity.ok(entradaService.findByFornecedorNomeFantasia(nomeFantasia));
    }

    @GetMapping("/byDataEntrada/{dataEntrada}")
    public ResponseEntity<List<?>> findByDataEntrada(
            @PathVariable("dataEntrada") LocalDate dataEntrada) {
        return ResponseEntity.ok(entradaService.findByDataEntrada(dataEntrada));
    }

    @GetMapping("/byNumeroNota/{numeroNota}")
    public ResponseEntity<?> findByDataEntrada(
            @PathVariable("numeroNota") String numeroNota) {
        return ResponseEntity.ok(entradaService.findByNumeroNota(numeroNota));
    }

    @GetMapping("/byValorTotal/{valorTotal}")
    public ResponseEntity<?> findByValorTotal(
            @PathVariable("valorTotal") BigDecimal valorTotal) {
        return ResponseEntity.ok(entradaService.findByValorTotalNota(valorTotal));
    }

    @DeleteMapping("/byNumeroNota/{numeroNota}")
    public ResponseEntity<?> deleteByNumeroNota(@PathVariable("numeroNota") String numeroNota) {
        entradaService.deleteByNumeroNota(numeroNota);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
