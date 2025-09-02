package com.api.menumaster.controller;

import com.api.menumaster.dtos.request.RequestAtualizarPedidoDto;
import com.api.menumaster.dtos.request.RequestCriarPedidoDto;
import com.api.menumaster.dtos.response.ResponsePedidoDto;
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

import static com.api.menumaster.controller.utils.AuxiliarRetornoAuthentication.getAuthentication;

@RestController
@RequestMapping("/pedido")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','PEDIDO_CREATE')")
    public ResponseEntity<ResponsePedidoDto> criarPedido(@RequestBody @Valid RequestCriarPedidoDto dto) {
        return ResponseEntity.ok(pedidoService.criarPedido(dto, getAuthentication()));
    }

    @PutMapping("/{idPedido}")
    @PreAuthorize("hasAnyAuthority('ADMIN','PEDIDO_EDIT')")
    public ResponseEntity<ResponsePedidoDto> editarPedido(@PathVariable("idPedido") UUID idPedido,
                                                          @RequestBody @Valid RequestAtualizarPedidoDto dto) {
        return ResponseEntity.ok(pedidoService.editarPedido(idPedido, dto, getAuthentication()));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','PEDIDO_READ')")
    public ResponseEntity<List<ResponsePedidoDto>> buscarTodosPedidos() {
        return ResponseEntity.ok(pedidoService.buscarTodosPedidos());
    }

    @GetMapping("/byDataEmissao")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PEDIDO_READ')")
    public ResponseEntity<List<ResponsePedidoDto>> buscarPedidoPorData(@RequestParam LocalDate dataEmissao) {
        return ResponseEntity.ok(pedidoService.buscarPedidoPorDataEmissao(dataEmissao));
    }

    @GetMapping("/byDataEmissaoBetween")
    @PreAuthorize("hasAnyAuthority('ADMIN','PEDIDO_READ')")
    public ResponseEntity<List<ResponsePedidoDto>> buscarPedidosPorDataBetween(@RequestParam(value = "dataInicial") LocalDate inicio,
                                                                               @RequestParam(value = "dataFim") LocalDate fim) {
        return ResponseEntity.ok(pedidoService.buscarPedidosPorDataEmissaoBetween(inicio, fim));
    }

    @GetMapping("byTotalPedido")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PEDIDO_READ')")
    public ResponseEntity<List<ResponsePedidoDto>> buscarPedidoPorTotal(@RequestParam BigDecimal valorPedido){
        return ResponseEntity.ok(pedidoService.buscarPedidoPorTotal(valorPedido));
    }

    @GetMapping("/byTotalPedidoBetween")
    @PreAuthorize("hasAnyAuthority('ADMIN',PEDIDO_READ')")
    public ResponseEntity<List<ResponsePedidoDto>> buscarPedidosPorTotalBetween(
            @RequestParam(value = "valorInicio", required = true) BigDecimal inicio,
            @RequestParam(value = "valorFim", required = false) BigDecimal fim) {
        return ResponseEntity.ok(pedidoService.buscarPedidoPorTotalBetween(inicio, fim));
    }

    @GetMapping("/byNumeroMesa/{numero}")
    @PreAuthorize("hasAnyAuthority('ADMIN','PEDIDO_READ')")
    public ResponseEntity<List<ResponsePedidoDto>> buscarPedidosPorMesa(@PathVariable("numero") Integer numero) {
        return ResponseEntity.ok(pedidoService.buscarPorMesa(numero));
    }

    @GetMapping("/byStatusPedido/{status}")
    @PreAuthorize("hasAnyAuthority('ADMIN',PEDIDO_READ')")
    public ResponseEntity<List<ResponsePedidoDto>> buscarPedidoPorStatus(@PathVariable("status") StatusPedido status) {
        return ResponseEntity.ok(pedidoService.buscarPedidoPorStatus(status));
    }
}
