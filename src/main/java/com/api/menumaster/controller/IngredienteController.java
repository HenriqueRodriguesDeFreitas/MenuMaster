package com.api.menumaster.controller;

import com.api.menumaster.dtos.request.RequestIngredienteDto;
import com.api.menumaster.dtos.request.RequestIngredienteUpdateDto;
import com.api.menumaster.dtos.response.ResponseIngredienteDto;
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
    @PreAuthorize("hasAnyAuthority('ADMIN', 'INGREDIENTE_CREATE')")
    public ResponseEntity<?> save(@RequestBody @Valid RequestIngredienteDto dto) {
        return ResponseEntity.ok(ingredienteService.salvarNovoIngrediente(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'INGREDIENTE_UPDATE')")
    public ResponseEntity<?> update(@PathVariable("id") UUID id, @RequestBody @Valid RequestIngredienteUpdateDto dto) {
        return ResponseEntity.ok(ingredienteService.atualizarIngrediente(id, dto));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'INGREDIENTE_READ')")
    public ResponseEntity<List<?>> findAll() {
        return ResponseEntity.ok(ingredienteService.buscarTodosIngredientes());
    }

    @GetMapping("/byCodigo")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'INGREDIENTE_READ')")
    public ResponseEntity<?> findByCodigo(@RequestParam Integer codigo) {
        return ResponseEntity.ok(ingredienteService.buscarIngredientePorCodigo(codigo));
    }

    @GetMapping("/byNome")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'INGREDIENTE_READ')")
    public ResponseEntity<List<?>> findByNome(@RequestParam String nome) {
        return ResponseEntity.ok(ingredienteService.buscarIngredientePorNome(nome));
    }

    @GetMapping("/byDescricao")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'INGREDIENTE_READ')")
    public ResponseEntity<List<?>> findByDescricao(@RequestParam String descricao) {
        return ResponseEntity.ok(ingredienteService.buscarIngredientePorDescricao(descricao));
    }

    @GetMapping("/byPrecoCusto")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'INGREDIENTE_READ')")
    public ResponseEntity<List<?>> findByPrecoCusto(@RequestParam BigDecimal precoCusto) {
        return ResponseEntity.ok(ingredienteService.buscarIngredientePorPrecoCusto(precoCusto));
    }

    @GetMapping("/byPrecoVenda")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'INGREDIENTE_READ')")
    public ResponseEntity<List<?>> findByPrecoVenda(@RequestParam BigDecimal precoVenda) {
        return ResponseEntity.ok(ingredienteService.buscarIngredientePorPrecoVenda(precoVenda));
    }

    @GetMapping("/byPrecoCustoBetween")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'INGREDIENTE_READ')")
    public ResponseEntity<List<ResponseIngredienteDto>> findByPrecoCustoBetween(@RequestParam BigDecimal precoInicial,
                                                                                @RequestParam BigDecimal precoFinal) {
        return ResponseEntity.ok(ingredienteService.buscarIngredientePorPrecoCustoBetween(precoInicial, precoFinal));
    }

    @GetMapping("/byPrecoVendaBetween")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'INGREDIENTE_READ')")
    public ResponseEntity<List<ResponseIngredienteDto>> findByPrecoVendaBetween(@RequestParam BigDecimal precoInicial,
                                                                                @RequestParam BigDecimal precoFinal) {
        return ResponseEntity.ok(ingredienteService.buscarIngredientePorPrecoVendaBetween(precoInicial, precoFinal));
    }

    @GetMapping("/byAtivo")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'INGREDIENTE_READ')")
    public ResponseEntity<List<?>> findByAtivos() {
        return ResponseEntity.ok(ingredienteService.buscarIngredienteIsAtivo());
    }

    @GetMapping("/byInativo")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'INGREDIENTE_READ')")
    public ResponseEntity<List<?>> findByNaoAtivos() {
        return ResponseEntity.ok(ingredienteService.buscarIngredienteIsInativo());
    }

    @GetMapping("/byIsAdicional")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'INGREDIENTE_READ')")
    public ResponseEntity<List<?>> findByIsAdicional() {
        return ResponseEntity.ok(ingredienteService.buscarIngredienteIsAdicional());
    }

    @GetMapping("/byIsNotAdicional")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'INGREDIENTE_READ')")
    public ResponseEntity<List<?>> findByNaoAdicional() {
        return ResponseEntity.ok(ingredienteService.buscarIngredienteIsNotAdicional());
    }

    @GetMapping("/byUnidadeMedida")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'INGREDIENTE_READ')")
    public ResponseEntity<List<?>> findByUnidadeMedia(@RequestParam UnidadeMedida unidadeMedida) {
        return ResponseEntity.ok(ingredienteService.buscarIngredienteUnidadeMedida(unidadeMedida));
    }

    @GetMapping("/byControlarEstoqueIsTrue")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'INGREDIENTE_READ')")
    public ResponseEntity<List<?>> findByControlarEstoqueIsTrue() {
        return ResponseEntity.ok(ingredienteService.buscarIngredienteControlarEstoqueIsTrue());
    }

    @GetMapping("/byControlarEstoqueIsFalse")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'INGREDIENTE_READ')")
    public ResponseEntity<List<?>> findByControlarEstoqueIsFalse() {
        return ResponseEntity.ok(ingredienteService.buscarIngredienteControlarEstoqueIsFalse());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'INGREDIENTE_DELETE')")
    public ResponseEntity<Void> deleteById(@RequestParam("id") UUID id) {
        ingredienteService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

}
