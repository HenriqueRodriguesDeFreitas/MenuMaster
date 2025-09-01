package com.api.menumaster.controller;

import com.api.menumaster.dtos.request.RequestAtualizarProdutoDto;
import com.api.menumaster.dtos.request.RequestCriaProdutoDto;
import com.api.menumaster.dtos.response.ResponseProdutoDto;
import com.api.menumaster.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PRODUTO_CREATE')")
    public ResponseEntity<ResponseProdutoDto> criarProduto(@RequestBody @Valid RequestCriaProdutoDto dto) {
        return ResponseEntity.ok(produtoService.criarProduto(dto));
    }

    @PutMapping("/{codigoProduto}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PRODUTO_UPDATE')")
    public ResponseEntity<ResponseProdutoDto> atualizarProduto(@PathVariable("codigoProduto") Long codigoProduto,
                                                               @RequestBody RequestAtualizarProdutoDto dto) {
        return ResponseEntity.ok(produtoService.atualizarProduto(codigoProduto, dto));
    }

    @GetMapping("byCodigo/{codigo}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PRODUTO_READ')")
    public ResponseEntity<ResponseProdutoDto> buscarPorCodigo(@PathVariable("codigo") Long codigo) {
        return ResponseEntity.ok(produtoService.findByCodigo(codigo));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PRODUTO_READ')")
    public ResponseEntity<List<ResponseProdutoDto>> findAll() {
        return ResponseEntity.ok(produtoService.findAll());
    }

    @GetMapping("/byNome/{nome}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PRODUTO_READ')")
    public ResponseEntity<List<?>> findByNome(@PathVariable("nome") String nome) {
        return ResponseEntity.ok(produtoService.findByNome(nome));
    }

    @GetMapping("/byPrecoCusto")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PRODUTO_READ')")
    public ResponseEntity<List<ResponseProdutoDto>> findByPrecoCusto(@RequestParam BigDecimal precoCusto) {
        return ResponseEntity.ok(produtoService.findByPrecoCusto(precoCusto));
    }

    @GetMapping("/byPrecoCustoBetween")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PRODUTO_READ')")
    public ResponseEntity<List<ResponseProdutoDto>> findByPrecoCustoBetween(@RequestParam BigDecimal precoCustoInicio,
                                                                            @RequestParam BigDecimal precoCustoFinal) {
        return ResponseEntity.ok(produtoService.findByPrecoCustoBetween(precoCustoInicio,
                precoCustoFinal));
    }

    @GetMapping("/byPrecoVenda")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PRODUTO_READ')")
    public ResponseEntity<List<ResponseProdutoDto>> findByPrecoVenda(@RequestParam BigDecimal precoVenda) {
        return ResponseEntity.ok(produtoService.findByPrecoVenda(precoVenda));
    }

    @GetMapping("/byPrecoVendaBetween")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PRODUTO_READ')")
    public ResponseEntity<List<ResponseProdutoDto>> findByPrecoVendaBetween(@RequestParam BigDecimal precoVendaInicio,
                                                                            @RequestParam BigDecimal precoVendaFinal) {
        return ResponseEntity.ok(produtoService.findByPrecoVendaBetween(precoVendaInicio,
                precoVendaFinal));
    }

    @GetMapping("/byAtivos")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PRODUTO_READ')")
    public ResponseEntity<List<ResponseProdutoDto>> findByProdutosAtivos() {
        return ResponseEntity.ok(produtoService.findByProdutosAtivos());
    }

    @GetMapping("/byInativos")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PRODUTO_READ')")
    public ResponseEntity<List<ResponseProdutoDto>> findByProdutosInativos() {
        return ResponseEntity.ok(produtoService.findByProdutosInativos());
    }

    @DeleteMapping("/{codigoProduto}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PRODUTO_DELETE')")
    public ResponseEntity<Void> deleteByCodigo(@PathVariable("codigoProduto") Long codigoProduto) {
        produtoService.deleteByCodigo(codigoProduto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
