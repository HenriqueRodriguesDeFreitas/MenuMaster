package com.api.menumaster.controller;

import com.api.menumaster.dtos.request.RequestAtualizarPedidoDto;
import com.api.menumaster.dtos.request.RequestCriarPedidoDto;
import com.api.menumaster.dtos.response.ResponseErroDto;
import com.api.menumaster.dtos.response.ResponsePedidoDto;
import com.api.menumaster.model.enums.StatusPedido;
import com.api.menumaster.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Pedidos", description = "Operações relacionadas a pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','PEDIDO_CREATE')")
    @Operation(summary = "Criar novo pedido", description = "Cria um pedido com os itens informados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido criado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponsePedidoDto.class))),
            @ApiResponse(responseCode = "409", description = "Conflito: estoque insuficiente ou caixa não aberto",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseErroDto.class),
                            examples = {@ExampleObject(name = "Usuário não possui caixa aberto para efetuar vendas", value = """
                                    {
                                    "localDateTime" : "2025-09-02T14:45:04",
                                    "httpsValue": 409,
                                    "erro": "Erro de conflito",
                                    "descricao": "Usuario não possui um caixa aberto."
                                    }
                                    """),
                                    @ExampleObject(name = "Produtos precisam ser adicionados ao pedido", value = """
                                            {
                                            "localDateTime" : "2025-09-02T14:45:04",
                                            "httpsValue": 409,
                                            "erro": "Erro de conflito",
                                            "descricao": "Produtos precisam ser adicionados no pedido"
                                            }
                                            """)}
                    )),
            @ApiResponse(responseCode = "404", description = "Não encontrado: objeto pesquisado não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseErroDto.class),
                            examples = {@ExampleObject(name = "Não encontrado produto procurado", value = """
                                    {
                                    "localDateTime" : "2025-09-02T14:45:04",
                                    "httpsValue": 404,
                                    "erro": "Erro de objeto não encontrado",
                                    "descricao": "Produto não encontrado."
                                    }
                                    """),
                                    @ExampleObject(name = "Não encontrado ingrediente procurado", value = """
                                            {
                                            "localDateTime" : "2025-09-02T14:45:04",
                                            "httpsValue": 404,
                                            "erro": "Erro de objeto não encontrado",
                                            "descricao": "Ingrediente não encontrado."
                                            }
                                            """)

                            })),
            @ApiResponse(responseCode = "422", description = "Produto X não possui estoque suficiente do ingrediente Y",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseErroDto.class),
                            examples = {@ExampleObject(name = "Produto não possui ingrediente suficiente para venda", value = """
                                    {
                                    "localDateTime" : "2025-09-02T14:45:04",
                                    "httpsValue": 422,
                                    "erro": "Entidade não processavel",
                                    "descricao": "Produto 'Hamburguer Simples' não possui estoque suficiente do ingrediente 'Pão'."
                                    }
                                    """)})),
            @ApiResponse(responseCode = "400", description = "Dados invalidos informados",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseErroDto.class),
                            examples = {@ExampleObject(name = "Quantidade de produtos precisa ser maior que zero", value = """
                                    {
                                    "localDateTime" : "2025-09-02T14:45:04",
                                    "httpsValue": 400,
                                    "erro": "Dados invalidos informados",
                                    "descricao": "Quantidade de produtos precisa ser preenchida com valor maior que zero"
                                    }
                                    """)}
                    )),
    })
    public ResponseEntity<ResponsePedidoDto> criarPedido(@RequestBody @Valid RequestCriarPedidoDto dto) {
        return ResponseEntity.ok(pedidoService.criarPedido(dto, getAuthentication()));
    }

    @PutMapping("/{idPedido}")
    @PreAuthorize("hasAnyAuthority('ADMIN','PEDIDO_EDIT')")
    @Operation(summary = "Atualizar pedido existente", description = "Atualiza os dados e itens de um pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponsePedidoDto.class))),

            @ApiResponse(responseCode = "409", description = "Conflito: estoque insuficiente ou caixa não aberto",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseErroDto.class),
                            examples = {@ExampleObject(name = "Usuário não possui caixa aberto para efetuar vendas", value = """
                                    {
                                    "localDateTime" : "2025-09-02T14:45:04",
                                    "httpsValue": 409,
                                    "erro": "Erro de conflito",
                                    "descricao": "Usuario não possui um caixa aberto."
                                    }
                                    """),
                                    @ExampleObject(name = "Produtos precisam ser adicionados ao pedido", value = """
                                            {
                                            "localDateTime" : "2025-09-02T14:45:04",
                                            "httpsValue": 409,
                                            "erro": "Erro de conflito",
                                            "descricao": "Produtos precisam ser adicionados no pedido"
                                            }
                                            """)}
                    )),
            @ApiResponse(responseCode = "404", description = "Não encontrado: objeto pesquisado não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseErroDto.class),
                            examples = {@ExampleObject(name = "Não encontrado produto procurado", value = """
                                    {
                                    "localDateTime" : "2025-09-02T14:45:04",
                                    "httpsValue": 404,
                                    "erro": "Erro de objeto não encontrado",
                                    "descricao": "Produto não encontrado."
                                    }
                                    """),
                                    @ExampleObject(name = "Não encontrado ingrediente procurado", value = """
                                            {
                                            "localDateTime" : "2025-09-02T14:45:04",
                                            "httpsValue": 404,
                                            "erro": "Erro de objeto não encontrado",
                                            "descricao": "Ingrediente não encontrado."
                                            }
                                            """),
                                    @ExampleObject(name = "Não encontrado pedido procurado", value = """
                                            {
                                            "localDateTime" : "2025-09-02T14:45:04",
                                            "httpsValue": 404,
                                            "erro": "Erro de objeto não encontrado",
                                            "descricao": "pedido não encontrado."
                                            }
                                            """)

                            })),
            @ApiResponse(responseCode = "422", description = "Produto X não possui estoque suficiente do ingrediente Y",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseErroDto.class),
                            examples = {@ExampleObject(name = "Produto não possui ingrediente suficiente para venda", value = """
                                    {
                                    "localDateTime" : "2025-09-02T14:45:04",
                                    "httpsValue": 422,
                                    "erro": "Entidade não processavel",
                                    "descricao": "Produto 'Hamburguer Simples' não possui estoque suficiente do ingrediente 'Pão'."
                                    }
                                    """)})),
            @ApiResponse(responseCode = "400", description = "Dados invalidos informados",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseErroDto.class),
                            examples = {@ExampleObject(name = "Quantidade de produtos precisa ser maior que zero", value = """
                                    {
                                    "localDateTime" : "2025-09-02T14:45:04",
                                    "httpsValue": 400,
                                    "erro": "Dados invalidos informados",
                                    "descricao": "Quantidade de produtos precisa ser preenchida com valor maior que zero"
                                    }
                                    """)}
                    )),
    })
    public ResponseEntity<ResponsePedidoDto> editarPedido(@PathVariable("idPedido") UUID idPedido,
                                                          @RequestBody @Valid RequestAtualizarPedidoDto dto) {
        return ResponseEntity.ok(pedidoService.editarPedido(idPedido, dto, getAuthentication()));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','PEDIDO_READ')")
    @Operation(summary = "Buscar por todos os pedidos", description = "Busca todos os pedidos na base de dados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca retornada com sucesso", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponsePedidoDto.class)))
    })
    public ResponseEntity<List<ResponsePedidoDto>> buscarTodosPedidos() {
        return ResponseEntity.ok(pedidoService.buscarTodosPedidos());
    }

    @GetMapping("/byDataEmissao")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PEDIDO_READ')")
    @Operation(summary = "Buscar pedidos por data especifica", description = "Busca pedidos por data especifica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca retornada com sucesso", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponsePedidoDto.class)))
    })
    public ResponseEntity<List<ResponsePedidoDto>> buscarPedidoPorData(@RequestParam LocalDate dataEmissao) {
        return ResponseEntity.ok(pedidoService.buscarPedidoPorDataEmissao(dataEmissao));
    }

    @GetMapping("/byDataEmissaoBetween")
    @PreAuthorize("hasAnyAuthority('ADMIN','PEDIDO_READ')")
    @Operation(summary = "Buscar pedidos entre intervalos de datas", description = "Busca pedidos entre intervalos de datas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca retornada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponsePedidoDto.class))),
            @ApiResponse(responseCode = "400", description = "Argumento passado incorreto",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseErroDto.class),
                            examples = {@ExampleObject(name = "Data de inicio ou fim nula", value = """
                                    {
                                                   "localDateTime" : "2025-09-02T14:45:04",
                                                   "httpsValue": 400,
                                                   "erro": "Dados invalidos informados",
                                                   "descricao": "Data de inicio ou fim não podem ser nulas"
                                    }
                                    """),
                                    @ExampleObject(name = "Data inicial não pode ser após a data final", value = """
                                            {
                                                           "localDateTime" : "2025-09-02T14:45:04",
                                                           "httpsValue": 400,
                                                           "erro": "Dados invalidos informados",
                                                           "descricao": "A data de inicio precisa ser anterior a data final."
                                            }
                                            """)}))
    })
    public ResponseEntity<List<ResponsePedidoDto>> buscarPedidosPorDataBetween(@RequestParam(value = "dataInicial") LocalDate inicio,
                                                                               @RequestParam(value = "dataFim") LocalDate fim) {
        return ResponseEntity.ok(pedidoService.buscarPedidosPorDataEmissaoBetween(inicio, fim));
    }

    @GetMapping("byTotalPedido")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PEDIDO_READ')")
    @Operation(summary = "Buscar pedidos por valor total", description = "Busca pedidos por valor total especifico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Buscar retornada com sucesso", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponsePedidoDto.class))),
            @ApiResponse(responseCode = "400", description = "Valor passado menor que zero",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseErroDto.class),
                            examples = {@ExampleObject(name = "Valor passado precisa ser maior que zero", value = """
                                            {
                                                   "localDateTime" : "2025-09-02T14:45:04",
                                                   "httpsValue": 400,
                                                   "erro": "Dados invalidos informados",
                                                   "descricao": "O valor precisa ser maior que zero"
                                    }
                                    """)}))
    })
    public ResponseEntity<List<ResponsePedidoDto>> buscarPedidoPorTotal(@RequestParam BigDecimal valorPedido) {
        return ResponseEntity.ok(pedidoService.buscarPedidoPorTotal(valorPedido));
    }

    @GetMapping("/byTotalPedidoBetween")
    @PreAuthorize("hasAnyAuthority('ADMIN','PEDIDO_READ')")
    @Operation(summary = "Buscar pedidos por valor total entre intervalo", description = "Busca pedidos por valor total entre intervalo de valores")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca retornada com sucesso", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponsePedidoDto.class))),
            @ApiResponse(responseCode = "400", description = "Erro de valores invalidos informados",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseErroDto.class),
                            examples = {@ExampleObject(name = "Valor inicial ou final nulo", value = """
                                    {
                                    "localDateTime" : "2025-09-02T14:45:04",
                                    "httpsValue" : "400",
                                    "erro" : "Dados invalidos informados",
                                    "descricao": "Valor de inicio ou fim não podem ser nulos"
                                    }
                                    """),
                                    @ExampleObject(name = "O primeiro valor passado precisa ser menor que o último", value = """
                                            {
                                            "localDateTime" : "2025-09-02T14:45:04",
                                            "httpsValue" : "400",
                                            "erro" : "Dados invalidos informados",
                                            "descricao": "O valor de inicio precisa ser menor que o do fim"
                                            }
                                            """)}))
    })
    public ResponseEntity<List<ResponsePedidoDto>> buscarPedidosPorTotalBetween(
            @RequestParam(value = "valorInicio", required = true) BigDecimal inicio,
            @RequestParam(value = "valorFim", required = false) BigDecimal fim) {
        return ResponseEntity.ok(pedidoService.buscarPedidoPorTotalBetween(inicio, fim));
    }

    @GetMapping("/byNumeroMesa/{numero}")
    @PreAuthorize("hasAnyAuthority('ADMIN','PEDIDO_READ')")
    @Operation(summary = "Buscar pedidos por mesa", description = "Busca pedidos pelo número da mesa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca retornada com sucesso", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponsePedidoDto.class)))
    })
    public ResponseEntity<List<ResponsePedidoDto>> buscarPedidosPorMesa(@PathVariable("numero") Integer numero) {
        return ResponseEntity.ok(pedidoService.buscarPorMesa(numero));
    }

    @GetMapping("/byStatusPedido/{status}")
    @PreAuthorize("hasAnyAuthority('ADMIN','PEDIDO_READ')")
    @Operation(summary = "Buscar pedidos por status", description = "Busca pedido pelo seu status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca retornada com sucesso", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponsePedidoDto.class)))
    })
    public ResponseEntity<List<ResponsePedidoDto>> buscarPedidoPorStatus(@PathVariable("status") StatusPedido status) {
        return ResponseEntity.ok(pedidoService.buscarPedidoPorStatus(status));
    }
}
