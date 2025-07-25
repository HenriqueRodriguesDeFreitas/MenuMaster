package com.api.menumaster.controller;

import com.api.menumaster.dtos.request.RequestAtualizarPedidoDto;
import com.api.menumaster.dtos.request.RequestCriarPedidoDto;
import com.api.menumaster.model.enums.StatusPedido;
import com.api.menumaster.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pedido")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'LIDER', 'OPERADOR')")
    public ResponseEntity<?> criarPedido(@RequestBody @Valid RequestCriarPedidoDto dto) {
        return ResponseEntity.ok(pedidoService.criarPedido(dto));
    }

    @PutMapping("/{idPedido}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'LIDER')")
    public ResponseEntity<?> atualizarPedido(@PathVariable("idPedido") UUID idPedido,
                                             @RequestBody @Valid RequestAtualizarPedidoDto dto) {
        return ResponseEntity.ok(pedidoService.atualizarPedido(idPedido, dto));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'LIDER')")
    public ResponseEntity<List<?>> buscarTodosPedidos() {
        return ResponseEntity.ok(pedidoService.buscarTodosPedidos());
    }

    @GetMapping("/byDataEmissao")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'LIDER')")
    public ResponseEntity<List<?>> buscarPedidosPorData(@RequestParam(value = "dataInicial", required = true) LocalDate inicio,
                                                        @RequestParam(value = "dataFim", required = false) LocalDate fim) {
        return ResponseEntity.ok(pedidoService.buscarPedidosPorDataEmissao(inicio, fim));
    }

    @GetMapping("/byTotalPedido")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'LIDER')")
    public ResponseEntity<List<?>> buscarPedidosPorTotal(
            @RequestParam(value = "valorInicio", required = true) BigDecimal inicio,
            @RequestParam(value = "valorFim", required = false) BigDecimal fim) {
        return ResponseEntity.ok(pedidoService.buscarPedidoPorTotal(inicio, fim));
    }

    @GetMapping("/byNumeroMesa/{numero}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'LIDER', 'OPERADOR')")
    public ResponseEntity<List<?>> buscarPedidosPorMesa(@PathVariable("numero") Integer numero) {
        return ResponseEntity.ok(pedidoService.buscarPorMesa(numero));
    }

    @GetMapping("/byStatusPedido/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'LIDER', 'OPERADOR')")
    public ResponseEntity<List<?>> buscarPedidoPorStatus(@PathVariable("status") StatusPedido status) {
        return ResponseEntity.ok(pedidoService.buscarPedidoPorStatus(status));
    }
}
