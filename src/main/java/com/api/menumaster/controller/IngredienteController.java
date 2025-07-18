package com.api.menumaster.controller;

import com.api.menumaster.dtos.request.RequestIngredienteDto;
import com.api.menumaster.dtos.request.RequestIngredienteUpdateDto;
import com.api.menumaster.model.enums.UnidadeMedida;
import com.api.menumaster.service.IngredienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> save(@RequestBody @Valid RequestIngredienteDto dto){
        return ResponseEntity.ok(ingredienteService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id")UUID id, @RequestBody @Valid RequestIngredienteUpdateDto dto){
        return ResponseEntity.ok(ingredienteService.update(id, dto));
    }

    @GetMapping
    public ResponseEntity<List<?>> findAll(){
        return ResponseEntity.ok(ingredienteService.findAll());
    }

    @GetMapping("/byCodigo")
    public ResponseEntity<?> findByCodigo(@RequestParam Integer codigo){
        return ResponseEntity.ok(ingredienteService.findByCodigo(codigo));
    }

    @GetMapping("/byNome")
    public ResponseEntity<List<?>> findByNome(@RequestParam String nome){
        return ResponseEntity.ok(ingredienteService.findByNome(nome));
    }

    @GetMapping("/byDescricao")
    public ResponseEntity<List<?>> findByDescricao(@RequestParam String descricao){
        return ResponseEntity.ok(ingredienteService.findByDescricao(descricao));
    }

    @GetMapping("/byPrecoCusto")
    public ResponseEntity<List<?>> findByPrecoCusto(@RequestParam BigDecimal precoCusto){
        return ResponseEntity.ok(ingredienteService.findByPrecoCusto(precoCusto));
    }

    @GetMapping("/byPrecoVenda")
    public ResponseEntity<List<?>> findByPrecoVenda(@RequestParam BigDecimal precoVenda){
        return ResponseEntity.ok(ingredienteService.findByPrecoVenda(precoVenda));
    }

    @GetMapping("/byAtivo")
    public ResponseEntity<List<?>> findByAtivos(){
        return ResponseEntity.ok(ingredienteService.findByIsAtivo());
    }

    @GetMapping("/byInativo")
    public ResponseEntity<List<?>> findByNaoAtivos(){
        return ResponseEntity.ok(ingredienteService.findByIsInativo());
    }

    @GetMapping("/byIsAdicional")
    public ResponseEntity<List<?>> findByIsAdicional(){
        return ResponseEntity.ok(ingredienteService.findByIsAdicional());
    }

    @GetMapping("/byIsNotAdicional")
    public ResponseEntity<List<?>> findByNaoAdicional(){
        return ResponseEntity.ok(ingredienteService.findByIsNotAdicional());
    }

    @GetMapping("/byUnidadeMedida")
    public ResponseEntity<List<?>> findByUnidadeMedia(@RequestParam UnidadeMedida unidadeMedida){
        return ResponseEntity.ok(ingredienteService.findByUnidadeMedida(unidadeMedida));
    }

    @GetMapping("/byControlarEstoqueIsTrue")
    public ResponseEntity<List<?>> findByControlarEstoqueIsTrue(){
        return ResponseEntity.ok(ingredienteService.findByControlarEstoqueIsTrue());
    }

    @GetMapping("/byControlarEstoqueIsFalse")
    public ResponseEntity<List<?>> findByControlarEstoqueIsFalse(){
        return ResponseEntity.ok(ingredienteService.findByControlarEstoqueIsFalse());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@RequestParam("id") UUID id){
        ingredienteService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

}
