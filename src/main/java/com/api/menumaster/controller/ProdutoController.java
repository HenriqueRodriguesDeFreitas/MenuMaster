package com.api.menumaster.controller;

import com.api.menumaster.dtos.request.RequestAtualizarProdutoDto;
import com.api.menumaster.dtos.request.RequestCriaProdutoDto;
import com.api.menumaster.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/produto")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @PostMapping
    public ResponseEntity<?> criarProduto(@RequestBody @Valid RequestCriaProdutoDto dto) {
        return ResponseEntity.ok(produtoService.criarProduto(dto));
    }

    @PutMapping("/{codigoProduto}")
    public ResponseEntity<?> atualizarProduto(@PathVariable("codigoProduto") Long codigoProduto,
                                              @RequestBody RequestAtualizarProdutoDto dto) {
        return ResponseEntity.ok(produtoService.atualizarProduto(codigoProduto, dto));
    }

    @GetMapping("byCodigo/{codigo}")
    public ResponseEntity<?> buscarPorCodigo(@PathVariable("codigo") Long codigo) {
        return ResponseEntity.ok(produtoService.findByCodigo(codigo));
    }

    @GetMapping
    public ResponseEntity<List<?>> findAll() {
        return ResponseEntity.ok(produtoService.findAll());
    }

    @GetMapping("/byNome/{nome}")
    public ResponseEntity<List<?>> findByNome(@PathVariable("nome") String nome) {
        return ResponseEntity.ok(produtoService.findByNome(nome));
    }

    @GetMapping("/byPrecoCusto")
    public ResponseEntity<List<?>> findByPrecoCustoBetween(@RequestParam BigDecimal precoCustoInicio,
                                                           @RequestParam BigDecimal precoCustoFinal) {
        return ResponseEntity.ok(produtoService.findByPrecoCustoBetween(precoCustoInicio,
                precoCustoFinal));
    }

    @GetMapping("/byPrecoVenda")
    public ResponseEntity<List<?>> findByPrecoVendaBetween(@RequestParam BigDecimal precoVendaInicio,
                                                           @RequestParam BigDecimal precoVendaFinal) {
        return ResponseEntity.ok(produtoService.findByPrecoVendaBetween(precoVendaInicio,
                precoVendaFinal));
    }

    @GetMapping("/byAtivos")
    public ResponseEntity<List<?>> findByProdutosAtivos() {
        return ResponseEntity.ok(produtoService.findByProdutosAtivos());
    }

    @GetMapping("/byInativos")
    public ResponseEntity<List<?>> findByProdutosInativos() {
        return ResponseEntity.ok(produtoService.findByProdutosInativos());
    }

    @DeleteMapping("/{codigoProduto}")
    public ResponseEntity<Void> deleteByCodigo(@PathVariable("codigoProduto") Long codigoProduto) {
        produtoService.deleteByCodigo(codigoProduto);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
