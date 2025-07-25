package com.api.menumaster.controller;

import com.api.menumaster.dtos.request.RequestIngredienteDto;
import com.api.menumaster.dtos.request.RequestIngredienteUpdateDto;
import com.api.menumaster.model.enums.UnidadeMedida;
import com.api.menumaster.service.IngredienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("ingrediente")
public class IngredienteController {

    private final IngredienteService ingredienteService;

    public IngredienteController(IngredienteService ingredienteService) {
        this.ingredienteService = ingredienteService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'LIDER')")
    public ResponseEntity<?> save(@RequestBody @Valid RequestIngredienteDto dto){
        return ResponseEntity.ok(ingredienteService.save(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'LIDER')")
    public ResponseEntity<?> update(@PathVariable("id")UUID id, @RequestBody @Valid RequestIngredienteUpdateDto dto){
        return ResponseEntity.ok(ingredienteService.update(id, dto));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'LIDER', 'OPERADOR')")
    public ResponseEntity<List<?>> findAll(){
        return ResponseEntity.ok(ingredienteService.findAll());
    }

    @GetMapping("/byCodigo")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'LIDER', 'OPERADOR')")
    public ResponseEntity<?> findByCodigo(@RequestParam Integer codigo){
        return ResponseEntity.ok(ingredienteService.findByCodigo(codigo));
    }

    @GetMapping("/byNome")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'LIDER', 'OPERADOR')")
    public ResponseEntity<List<?>> findByNome(@RequestParam String nome){
        return ResponseEntity.ok(ingredienteService.findByNome(nome));
    }

    @GetMapping("/byDescricao")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'LIDER', 'OPERADOR')")
    public ResponseEntity<List<?>> findByDescricao(@RequestParam String descricao){
        return ResponseEntity.ok(ingredienteService.findByDescricao(descricao));
    }

    @GetMapping("/byPrecoCusto")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'LIDER')")
    public ResponseEntity<List<?>> findByPrecoCusto(@RequestParam BigDecimal precoCusto){
        return ResponseEntity.ok(ingredienteService.findByPrecoCusto(precoCusto));
    }

    @GetMapping("/byPrecoVenda")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'LIDER', 'OPERADOR')")
    public ResponseEntity<List<?>> findByPrecoVenda(@RequestParam BigDecimal precoVenda){
        return ResponseEntity.ok(ingredienteService.findByPrecoVenda(precoVenda));
    }

    @GetMapping("/byAtivo")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'LIDER')")
    public ResponseEntity<List<?>> findByAtivos(){
        return ResponseEntity.ok(ingredienteService.findByIsAtivo());
    }

    @GetMapping("/byInativo")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'LIDER')")
    public ResponseEntity<List<?>> findByNaoAtivos(){
        return ResponseEntity.ok(ingredienteService.findByIsInativo());
    }

    @GetMapping("/byIsAdicional")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'LIDER')")
    public ResponseEntity<List<?>> findByIsAdicional(){
        return ResponseEntity.ok(ingredienteService.findByIsAdicional());
    }

    @GetMapping("/byIsNotAdicional")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'LIDER')")
    public ResponseEntity<List<?>> findByNaoAdicional(){
        return ResponseEntity.ok(ingredienteService.findByIsNotAdicional());
    }

    @GetMapping("/byUnidadeMedida")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'LIDER', 'OPERADOR')")
    public ResponseEntity<List<?>> findByUnidadeMedia(@RequestParam UnidadeMedida unidadeMedida){
        return ResponseEntity.ok(ingredienteService.findByUnidadeMedida(unidadeMedida));
    }

    @GetMapping("/byControlarEstoqueIsTrue")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'LIDER')")
    public ResponseEntity<List<?>> findByControlarEstoqueIsTrue(){
        return ResponseEntity.ok(ingredienteService.findByControlarEstoqueIsTrue());
    }

    @GetMapping("/byControlarEstoqueIsFalse")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'LIDER')")
    public ResponseEntity<List<?>> findByControlarEstoqueIsFalse(){
        return ResponseEntity.ok(ingredienteService.findByControlarEstoqueIsFalse());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'LIDER')")
    public ResponseEntity<Void> deleteById(@RequestParam("id") UUID id){
        ingredienteService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

}
