package com.api.menumaster.service;

import com.api.menumaster.dtos.request.RequestAtualizarPedidoDto;
import com.api.menumaster.dtos.request.RequestCriarPedidoDto;
import com.api.menumaster.dtos.request.RequestItemPedidoDto;
import com.api.menumaster.dtos.response.ResponseItemPedidoDto;
import com.api.menumaster.dtos.response.ResponsePedidoDto;
import com.api.menumaster.mappper.PedidoMapper;
import com.api.menumaster.model.*;
import com.api.menumaster.model.enums.StatusPedido;
import com.api.menumaster.model.enums.UnidadeMedida;
import com.api.menumaster.repository.CaixaRepository;
import com.api.menumaster.repository.IngredienteRepository;
import com.api.menumaster.repository.PedidoRepository;
import com.api.menumaster.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private CaixaRepository caixaRepository;
    @Mock
    private ProdutoRepository produtoRepository;
    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private IngredienteRepository ingredienteRepository;
    @Mock(lenient = true)
    private Authentication authentication;
    @InjectMocks
    private PedidoService pedidoService;
    @Mock
    private PedidoMapper pedidoMapper;

    private Caixa caixaAberto;
    private Produto produtoSalvo;
    private Tesouraria tesourariaAberta;
    private Ingrediente ingredienteSalvo;
    private Pedido pedidoSalvo;
    private ItemPedido itemPedidoSalvo;
    private ResponsePedidoDto responsePedidoSalvoDto;
    private ResponseItemPedidoDto responseItemPedidoDto;
    LocalDateTime hoje;

    @BeforeEach
    void setup() {
        hoje = LocalDateTime.now();

        tesourariaAberta = new Tesouraria(BigDecimal.ZERO, authentication.getName());
        tesourariaAberta.setId(UUID.randomUUID());
        tesourariaAberta.setDataAbertura(hoje);
        tesourariaAberta.setDataFechamento(null);
        tesourariaAberta.setUsuarioFechamento(authentication.getName());

        caixaAberto = new Caixa();
        caixaAberto.setId(UUID.randomUUID());
        caixaAberto.setDataAbertura(hoje);
        caixaAberto.setSaldoInicial(BigDecimal.ZERO);
        caixaAberto.setUsuarioUtilizando(authentication.getName());
        caixaAberto.setTesouraria(tesourariaAberta);

        tesourariaAberta.getCaixas().add(caixaAberto);

        produtoSalvo = new Produto();
        produtoSalvo.setId(UUID.randomUUID());
        produtoSalvo.setCodigoProduto(1L);
        produtoSalvo.setNome("produtoTeste");
        produtoSalvo.setDescricao("produtoTeste");
        produtoSalvo.setPrecoCusto(BigDecimal.valueOf(20));
        produtoSalvo.setPrecoVenda(BigDecimal.valueOf(40));
        produtoSalvo.setAtivo(true);
        produtoSalvo.setQuantidadeVendida(BigDecimal.ZERO);
        produtoSalvo.setUnidadeMedida(UnidadeMedida.UN);


        ingredienteSalvo = new Ingrediente();
        ingredienteSalvo.setCodigo(1);
        ingredienteSalvo.setAtivo(true);
        ingredienteSalvo.setNome("ingredienteTeste");
        ingredienteSalvo.setEstoque(BigDecimal.TWO);
        ingredienteSalvo.setControlarEstoque(false);
        ingredienteSalvo.setPrecoCusto(BigDecimal.valueOf(20));
        ingredienteSalvo.setPrecoVenda(BigDecimal.valueOf(20));
        ingredienteSalvo.setUnidadeMedida(UnidadeMedida.UN);
        ingredienteSalvo.setAdicional(false);


        pedidoSalvo = new Pedido();
        pedidoSalvo.setId(UUID.randomUUID());
        pedidoSalvo.setDataEmissao(LocalDateTime.now());
        pedidoSalvo.setTotalPedido(BigDecimal.valueOf(40));
        pedidoSalvo.setStatusPedido(StatusPedido.AGUARDANDO);
        pedidoSalvo.setNomeCliente("cliente1");
        pedidoSalvo.setEndereco("endereço1");
        pedidoSalvo.setContato("contato1");
        pedidoSalvo.setUsuarioCriou(authentication.getName());
        pedidoSalvo.setCaixa(caixaAberto);


        IngredienteProduto ingredienteProduto = new IngredienteProduto();
        ingredienteProduto.setIngrediente(ingredienteSalvo);
        ingredienteProduto.setQuantidade(BigDecimal.ONE);

        itemPedidoSalvo = new ItemPedido(BigDecimal.valueOf(1), pedidoSalvo, produtoSalvo);

        pedidoSalvo.setItensAssociados(new ArrayList<>(List.of(itemPedidoSalvo)));

        produtoSalvo.setItensProduto(List.of(itemPedidoSalvo));
        produtoSalvo.setIngredientesAssociados(List.of(ingredienteProduto));

        responseItemPedidoDto = new ResponseItemPedidoDto(produtoSalvo.getNome(), itemPedidoSalvo.getQuantidadeProduto(),
                produtoSalvo.getPrecoVenda());

        responsePedidoSalvoDto = new ResponsePedidoDto(
                pedidoSalvo.getId(),
                pedidoSalvo.getMesa(), pedidoSalvo.getStatusPedido(), pedidoSalvo.getDataEmissao(),
                pedidoSalvo.getDataEdicao(), pedidoSalvo.getNomeCliente(), pedidoSalvo.getEndereco(),
                pedidoSalvo.getContato(), pedidoSalvo.getObservacao(), pedidoSalvo.getTotalPedido(),
                List.of(responseItemPedidoDto)
        );
        when(authentication.getName()).thenReturn("usuarioTeste");
    }

    @Test
    void criarPedido_controlarEstoqueIngredienteFalse_deveRetornarResponsePedidoDto_quandoSucesso() {
        RequestItemPedidoDto requestItemPedidoDto = new RequestItemPedidoDto(1L, BigDecimal.ONE);
        RequestCriarPedidoDto requestCriarPedidoDto = new RequestCriarPedidoDto(1, "cliente1",
                "endereço1", "contato1",
                "observacao1", List.of(requestItemPedidoDto));


        when(pedidoMapper.toResponse(any(Pedido.class))).thenReturn(responsePedidoSalvoDto);
        when(caixaRepository.findByUsuarioUtilizandoAndDataFechamentoIsNull(
                authentication.getName())).thenReturn(Optional.of(caixaAberto));
        when(produtoRepository.findByCodigoProduto(1L)).thenReturn(Optional.of(produtoSalvo));
        when(ingredienteRepository.findByCodigo(1)).thenReturn(Optional.of(ingredienteSalvo));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoSalvo);

        responsePedidoSalvoDto = pedidoService.criarPedido(requestCriarPedidoDto, authentication);

        validandoAsserts(responsePedidoSalvoDto, pedidoSalvo);

        verify(caixaRepository, times(1))
                .findByUsuarioUtilizandoAndDataFechamentoIsNull(authentication.getName());
        verify(produtoRepository, times(1)).findByCodigoProduto(1L);
        verify(ingredienteRepository, times(1)).findByCodigo(1);
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
        verify(pedidoMapper, times(1)).toResponse(any(Pedido.class));
        verifyNoMoreInteractions(caixaRepository);
        verifyNoMoreInteractions(ingredienteRepository);
        verifyNoMoreInteractions(pedidoRepository);
        verifyNoMoreInteractions(pedidoMapper);
    }

    @Test
    void criarPedido_controlarEstoqueIngredienteTrue_deveRetornarResponsePedidoDto_quandoSucesso() {
        RequestItemPedidoDto requestItemPedidoDto = new RequestItemPedidoDto(1L, BigDecimal.ONE);
        RequestCriarPedidoDto requestCriarPedidoDto = new RequestCriarPedidoDto(1, "cliente1",
                "endereço1", "contato1",
                "observacao1", List.of(requestItemPedidoDto));

        ingredienteSalvo.setControlarEstoque(true);

        when(pedidoMapper.toResponse(any(Pedido.class))).thenReturn(responsePedidoSalvoDto);
        when(caixaRepository.findByUsuarioUtilizandoAndDataFechamentoIsNull(
                authentication.getName())).thenReturn(Optional.of(caixaAberto));
        when(produtoRepository.findByCodigoProduto(1L)).thenReturn(Optional.of(produtoSalvo));
        when(ingredienteRepository.findByCodigo(1)).thenReturn(Optional.of(ingredienteSalvo));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoSalvo);
        when(ingredienteRepository.save(any(Ingrediente.class))).thenReturn(ingredienteSalvo);

        System.out.println("Estoque antigo: " + ingredienteSalvo.getEstoque());

        responsePedidoSalvoDto = pedidoService.criarPedido(requestCriarPedidoDto, authentication);

        System.out.println("Estoque atual: " + ingredienteSalvo.getEstoque());

        validandoAsserts(responsePedidoSalvoDto, pedidoSalvo);

        verify(caixaRepository, times(1))
                .findByUsuarioUtilizandoAndDataFechamentoIsNull(authentication.getName());
        verify(produtoRepository, times(1)).findByCodigoProduto(1L);
        verify(ingredienteRepository, times(1)).findByCodigo(1);
        verify(ingredienteRepository, times(1)).save(any(Ingrediente.class));
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
        verify(pedidoMapper, times(1)).toResponse(any(Pedido.class));
        verifyNoMoreInteractions(caixaRepository);
        verifyNoMoreInteractions(ingredienteRepository);
        verifyNoMoreInteractions(pedidoRepository);
        verifyNoMoreInteractions(pedidoMapper);
    }

    @Test
    void editarPedido_controlarEstoqueIngredienteFalse_deveRetornarResponsePedidoDto_quandoSucesso() {
        UUID idParaBusca = pedidoSalvo.getId();

        RequestItemPedidoDto requestItemPedidoDto =
                new RequestItemPedidoDto(1L, BigDecimal.ONE);
        RequestAtualizarPedidoDto requestAtualizarPedidoDto = new RequestAtualizarPedidoDto(
                2, "cliente2",
                "endereço2", "contato2",
                StatusPedido.PREPARANDO, "observação2",
                List.of(requestItemPedidoDto));

        pedidoSalvo.setDataEdicao(hoje);
        pedidoSalvo.setTotalPedido(BigDecimal.valueOf(40));
        pedidoSalvo.setStatusPedido(requestAtualizarPedidoDto.status());
        pedidoSalvo.setNomeCliente(requestAtualizarPedidoDto.nomeCliente());
        pedidoSalvo.setEndereco(requestAtualizarPedidoDto.endereco());
        pedidoSalvo.setContato(requestAtualizarPedidoDto.contato());
        pedidoSalvo.setObservacao(requestAtualizarPedidoDto.observacao());

        ResponsePedidoDto responseAtualizado = new ResponsePedidoDto(
                pedidoSalvo.getId(),
                pedidoSalvo.getMesa(),
                pedidoSalvo.getStatusPedido(),
                pedidoSalvo.getDataEmissao(),
                pedidoSalvo.getDataEdicao(),
                pedidoSalvo.getNomeCliente(),
                pedidoSalvo.getEndereco(),
                pedidoSalvo.getContato(),
                pedidoSalvo.getObservacao(),
                pedidoSalvo.getTotalPedido(),
                List.of(responseItemPedidoDto)
        );

        when(pedidoRepository.findById(idParaBusca)).thenReturn(Optional.of(pedidoSalvo));
        when(caixaRepository.findByUsuarioUtilizandoAndDataFechamentoIsNull(authentication.getName()))
                .thenReturn(Optional.of(caixaAberto));
        when(produtoRepository.findByCodigoProduto(1L)).thenReturn(Optional.of(produtoSalvo));
        when(ingredienteRepository.findByCodigo(1)).thenReturn(Optional.of(ingredienteSalvo));
        when(pedidoMapper.toResponse(any(Pedido.class))).thenReturn(responseAtualizado);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoSalvo);

        ResponsePedidoDto response =
                pedidoService.editarPedido(idParaBusca, requestAtualizarPedidoDto, authentication);

        validandoAsserts(response, pedidoSalvo);

        verify(pedidoRepository, times(1)).findById(idParaBusca);
        verify(produtoRepository, times(1)).findByCodigoProduto(1L);
        verify(ingredienteRepository, times(2)).findByCodigo(1);
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
        verify(pedidoMapper, times(1)).toResponse(any(Pedido.class));
        verifyNoMoreInteractions(pedidoRepository, produtoRepository, ingredienteRepository, pedidoMapper);

    }

    @Test
    void buscarPedidosPorDataEmissaoBetween_deveRetornarListaDeResponsePedidoDto_quandoSucesso() {
        LocalDateTime dataHoraInicio = LocalDateTime.of(2025, 2, 1, 0, 0, 0);
        LocalDateTime dataHoraFim = LocalDateTime.of(2026, 2, 1, 23, 59, 59);
        LocalDate dataInicio = LocalDate.of(2025, 2, 1);
        LocalDate dataFim = LocalDate.of(2026, 2, 1);

        List<Pedido> pedidos = retornarListaDePedidos();

        when(pedidoRepository.findByDataEmissaoBetween(dataHoraInicio, dataHoraFim)).thenReturn(pedidos);
        when(pedidoMapper.toResponse(any(Pedido.class)))
                .thenAnswer(invocation -> {
                    Pedido p = invocation.getArgument(0);
                    return new ResponsePedidoDto(
                            p.getId(),
                            p.getMesa(),
                            p.getStatusPedido(),
                            p.getDataEmissao(),
                            p.getDataEdicao(),
                            p.getNomeCliente(),
                            p.getEndereco(),
                            p.getContato(),
                            p.getObservacao(),
                            p.getTotalPedido(),
                            List.of()
                    );
                });


        List<ResponsePedidoDto> response = pedidoService
                .buscarPedidosPorDataEmissaoBetween(dataInicio, dataFim);

        assertFalse(response.isEmpty(), "A lista de resposta não pode estar vazia");
        assertEquals(response.get(0).id(), pedidos.get(0).getId(), "id não coincide");

    }

    @Test
    void buscarPedidosPorDataEmissaoBetween_deveRetornarIllegalArgumentException_quandoDataInicioAnteriorADataFinal() {
        LocalDate dataInicio = LocalDate.of(2026, 2, 2);
        LocalDate dataFim = LocalDate.of(2026, 2, 1);

        List<Pedido> pedidos = retornarListaDePedidos();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> pedidoService.buscarPedidosPorDataEmissaoBetween(dataInicio, dataFim));

        assertEquals("A data de inicio precisa ser anterior a data final.", exception.getMessage(),
                "mensagens de erro não coincidem");
    }

    @Test
    void buscarPedidosPorDataEmissaoBetween_deveRetornarIllegalArgumentException_quandoDataInicioNula() {
        LocalDate dataFim = LocalDate.of(2026, 2, 1);

        List<Pedido> pedidos = retornarListaDePedidos();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> pedidoService.buscarPedidosPorDataEmissaoBetween(null, dataFim));

        assertEquals("Data de inicio ou fim não pode ser nula.", exception.getMessage(),
                "mensagens de erro não coincidem");
    }

    @Test
    void buscarPedidoPorTotalBetween_deveRetornarResponsePedidoDto_quandoSucesso() {
        BigDecimal valorInicio = BigDecimal.valueOf(20);
        BigDecimal valorFim = BigDecimal.valueOf(100);
        List<Pedido> pedidos = retornarListaDePedidos();

        when(pedidoRepository
                .findByTotalPedidoBetweenOrderByDataEmissao(valorInicio, valorFim)).thenReturn(pedidos);
        when(pedidoMapper.toResponse(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido p = invocation.getArgument(0);
            return new ResponsePedidoDto(
                    p.getId(),
                    p.getMesa(),
                    p.getStatusPedido(),
                    p.getDataEmissao(),
                    p.getDataEdicao(),
                    p.getNomeCliente(),
                    p.getEndereco(),
                    p.getContato(),
                    p.getObservacao(),
                    p.getTotalPedido(),
                    List.of()
            );
        });

        List<ResponsePedidoDto> response = pedidoService.buscarPedidoPorTotalBetween(valorInicio, valorFim);
        assertFalse(response.isEmpty(), "A lista de resposta não pode estar vazia");
        assertEquals(response.get(0).id(), pedidos.get(0).getId(), "id não coincide");
        assertEquals(response.get(0).mesa(), pedidos.get(0).getMesa(), "mesa não coincide");
        assertEquals(response.get(0).status(), pedidos.get(0).getStatusPedido(), "status não coincide");
        assertEquals(response.get(0).emissao().truncatedTo(ChronoUnit.SECONDS),
                pedidos.get(0).getDataEmissao().truncatedTo(ChronoUnit.SECONDS), "data emissão não coincide");
        if(pedidoSalvo.getDataEdicao() != null){
            assertEquals(response.get(0).editado().truncatedTo(ChronoUnit.SECONDS),
                    pedidos.get(0).getDataEdicao().truncatedTo(ChronoUnit.SECONDS), "data edição não coincide");
        }else{
            assertEquals(null, responsePedidoSalvoDto.editado(), "data edição deve ser nulo.");
        }
    }

    @Test
    void buscarPedidoPorTotalBetween_deveRetornarIllegalArgumentException_quandoValorInicialMaiorQueFinal(){
        BigDecimal valorInicial = BigDecimal.valueOf(20);
        BigDecimal valorFinal = BigDecimal.valueOf(10);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                ()-> pedidoService.buscarPedidoPorTotalBetween(valorInicial, valorFinal));

        assertEquals("O valor de inicio precisa ser menor que o do fim", exception.getMessage(),
                "mensagem de erro não coincide");
    }

    private void validandoAsserts(ResponsePedidoDto responsePedidoSalvoDto, Pedido pedidoSalvo) {
        assertEquals(responsePedidoSalvoDto.id(), pedidoSalvo.getId(), "id não coincide");
        assertEquals(responsePedidoSalvoDto.mesa(), pedidoSalvo.getMesa(), "mesa não coincide");
        assertEquals(responsePedidoSalvoDto.status(), pedidoSalvo.getStatusPedido(), "status não coincide");
        assertEquals(responsePedidoSalvoDto.emissao().truncatedTo(ChronoUnit.SECONDS),
                pedidoSalvo.getDataEmissao().truncatedTo(ChronoUnit.SECONDS), "data emissão não coincide");
        if (pedidoSalvo.getDataEdicao() != null) {
            assertEquals(responsePedidoSalvoDto.editado().truncatedTo(ChronoUnit.SECONDS),
                    pedidoSalvo.getDataEdicao().truncatedTo(ChronoUnit.SECONDS),
                    "data edição não coincide");
        } else {
            assertEquals(null, responsePedidoSalvoDto.editado(),
                    "data edição deve ser null");
        }

        assertEquals(responsePedidoSalvoDto.nomeCliente(), pedidoSalvo.getNomeCliente(), "nome do cliente não coincide");
        assertEquals(responsePedidoSalvoDto.endereco(), pedidoSalvo.getEndereco(), "endereço não coincide");
        assertEquals(responsePedidoSalvoDto.contato(), pedidoSalvo.getContato(), "contato não coincide");
        assertEquals(responsePedidoSalvoDto.observacao(), pedidoSalvo.getObservacao(), "observação não coincide");
        assertEquals(responsePedidoSalvoDto.totalPedido(), pedidoSalvo.getTotalPedido(), "total do pedido não coincide");
        assertEquals(responsePedidoSalvoDto.itens().getFirst().nomeProduto(),
                pedidoSalvo.getItensAssociados().getFirst().getProduto().getNome(),
                "nome do produto/item do pedido não coincide");
        assertEquals(responsePedidoSalvoDto.itens().getFirst().qtdProduto(),
                pedidoSalvo.getItensAssociados().getFirst().getQuantidadeProduto(),
                "quantidade do produto/item do pedido não coincide");
        assertEquals(responsePedidoSalvoDto.itens().getFirst().precoUnitario(),
                pedidoSalvo.getItensAssociados().getFirst().getProduto().getPrecoVenda(),
                "preço de venda do produto/item do pedido não coincide");
    }

    private List<Pedido> retornarListaDePedidos() {
        Pedido pedido1 = new Pedido();
        pedido1.setId(UUID.randomUUID());
        pedido1.setDataEmissao(
                LocalDateTime.of(2025, 1, 1, 10, 10, 10));
        pedido1.setTotalPedido(BigDecimal.valueOf(40));
        pedido1.setStatusPedido(StatusPedido.AGUARDANDO);
        pedido1.setNomeCliente("cliente1");
        pedido1.setEndereco("endereço1");
        pedido1.setContato("contato1");
        pedido1.setUsuarioCriou(authentication.getName());
        pedido1.setCaixa(caixaAberto);

        Pedido pedido2 = new Pedido();
        pedido2.setId(UUID.randomUUID());
        pedido2.setDataEmissao(
                LocalDateTime.of(2025, 3, 30, 10, 10, 10));
        pedido2.setTotalPedido(BigDecimal.valueOf(120));
        pedido2.setStatusPedido(StatusPedido.AGUARDANDO);
        pedido2.setNomeCliente("cliente1");
        pedido2.setEndereco("endereço1");
        pedido2.setContato("contato1");
        pedido2.setUsuarioCriou(authentication.getName());
        pedido2.setCaixa(caixaAberto);

        return List.of(pedido1, pedido2);
    }
}