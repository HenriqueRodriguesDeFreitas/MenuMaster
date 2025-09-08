package com.api.menumaster.service;

import com.api.menumaster.dtos.request.RequestAtualizarPedidoDto;
import com.api.menumaster.dtos.request.RequestCriarPedidoDto;
import com.api.menumaster.dtos.response.ResponsePedidoDto;
import com.api.menumaster.exception.custom.ConflictEntityException;
import com.api.menumaster.exception.custom.ConflictTesourariaException;
import com.api.menumaster.exception.custom.EntityNotFoundException;
import com.api.menumaster.exception.custom.EstoqueInsuficienteException;
import com.api.menumaster.mappper.PedidoMapper;
import com.api.menumaster.model.*;
import com.api.menumaster.model.enums.StatusPedido;
import com.api.menumaster.repository.CaixaRepository;
import com.api.menumaster.repository.IngredienteRepository;
import com.api.menumaster.repository.PedidoRepository;
import com.api.menumaster.repository.ProdutoRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final IngredienteRepository ingredienteRepository;
    private final CaixaRepository caixaRepository;
    private final PedidoMapper pedidoMapper;
    private static final LocalTime TEMPO_PADRAO_INICIO_DIA = LocalTime.of(0, 0, 0);

    public PedidoService(PedidoRepository pedidoRepository,
                         ProdutoRepository produtoRepository,
                         IngredienteRepository ingredienteRepository,
                         CaixaRepository caixaRepository,
                         PedidoMapper pedidoMapper) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
        this.ingredienteRepository = ingredienteRepository;
        this.caixaRepository = caixaRepository;
        this.pedidoMapper = pedidoMapper;
    }

    @Transactional
    public ResponsePedidoDto criarPedido(RequestCriarPedidoDto dto, Authentication authentication) {
        Caixa caixa = validarCaixaAberto(authentication.getName());

        Pedido pedido = new Pedido();
        pedido.setDataEmissao(LocalDateTime.now());
        pedido.setDataEdicao(LocalDateTime.now());
        pedido.setNomeCliente(dto.nomeCliente());
        pedido.setEndereco(dto.endereco());
        pedido.setContato(dto.contato());
        pedido.setObservacao(dto.observacao());
        pedido.setUsuarioCriou(authentication.getName());
        pedido.setStatusPedido(StatusPedido.AGUARDANDO);

        if (dto.mesa() == null || dto.mesa() < 0) {
            pedido.setMesa(0);
        }

        List<ItemPedido> itens = new ArrayList<>();
        processarItensDoPedido(dto, pedido, itens);

        pedido.setItensAssociados(itens);
        pedido.ajustarQuantidadeParaUnidade();
        pedido.calcularTotalPedido();

        pedido.setCaixa(caixa);
        caixa.getPedidos().add(pedido);
        return converteEntidadeParaDto(pedidoRepository.save(pedido));
    }

    @Transactional
    public ResponsePedidoDto editarPedido(UUID idPedido, RequestAtualizarPedidoDto dto,
                                          Authentication authentication) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new ConflictEntityException("Pedido não encontrado"));

        Caixa caixaAtual = validarCaixaAberto(authentication.getName());

        validarPedidoPertenceMesmoCaixa(pedido, caixaAtual);

        List<ItemPedido> itensOriginais = new ArrayList<>(pedido.getItensAssociados());

        pedido.setDataEdicao(LocalDateTime.now());
        pedido.setNomeCliente(dto.nomeCliente());
        pedido.setEndereco(dto.endereco());
        pedido.setContato(dto.contato());
        pedido.setObservacao(dto.observacao());
        pedido.setStatusPedido(dto.status());
        pedido.setUsuarioEditou(authentication.getName());
        if (dto.mesa() == null || dto.mesa() < 0) {
            pedido.setMesa(0);
        }


        List<ItemPedido> novosItens = new ArrayList<>();

        processarItensDoPedido(dto, pedido, novosItens);

        atualizarEstoqueComDiferenca(itensOriginais, novosItens);

        pedido.getItensAssociados().clear();
        pedido.getItensAssociados().addAll(novosItens);

        pedido.ajustarQuantidadeParaUnidade();
        pedido.calcularTotalPedido();


        return converteEntidadeParaDto(pedidoRepository.save(pedido));
    }

    public List<ResponsePedidoDto> buscarTodosPedidos() {
        List<Pedido> pedidos = pedidoRepository.findAll();
        return converteEntidadeParaDto(pedidos);
    }

    public List<ResponsePedidoDto> buscarPedidoPorDataEmissao(LocalDate dataEmissao) {
        LocalDateTime dataHoraEmissao = LocalDateTime.of(dataEmissao.getYear(), dataEmissao.getMonth(),
                dataEmissao.getDayOfMonth(), TEMPO_PADRAO_INICIO_DIA.getHour(),
                TEMPO_PADRAO_INICIO_DIA.getMinute(), TEMPO_PADRAO_INICIO_DIA.getSecond());

        return converteEntidadeParaDto(pedidoRepository.findByDataEmissao(dataHoraEmissao));
    }

    public List<ResponsePedidoDto> buscarPedidosPorDataEmissaoBetween(LocalDate dataInicio,
                                                                      LocalDate dataFim) {
        if (dataInicio == null || dataFim == null)
            throw new IllegalArgumentException("Data de inicio ou fim não pode ser nula.");

        if (dataInicio.isAfter(dataFim))
            throw new IllegalArgumentException("A data de inicio precisa ser anterior a data final.");

        LocalDateTime dataHoraPedidoInicio = LocalDateTime.of(dataInicio.getYear(), dataInicio.getMonth(),
                dataInicio.getDayOfMonth(), TEMPO_PADRAO_INICIO_DIA.getHour(),
                TEMPO_PADRAO_INICIO_DIA.getMinute(), TEMPO_PADRAO_INICIO_DIA.getSecond());


        LocalDateTime dataHoraPedidoFim = LocalDateTime.of(dataFim.getYear(), dataFim.getMonth(),
                dataFim.getDayOfMonth(), 23, 59, 59);

        List<Pedido> pedidos = pedidoRepository.findByDataEmissaoBetween(dataHoraPedidoInicio, dataHoraPedidoFim);

        return converteEntidadeParaDto(pedidos);
    }

    public List<ResponsePedidoDto> buscarPorMesa(Integer mesa) {
        List<Pedido> pedidos = pedidoRepository.findByMesaOrderByDataEmissao(mesa);
        return converteEntidadeParaDto(pedidos);
    }

    public List<ResponsePedidoDto> buscarPedidoPorTotal(BigDecimal valorTotalPedido) {
        if (valorTotalPedido.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("o valor precisa ser maior que zero.");

        return converteEntidadeParaDto(pedidoRepository
                .findByTotalPedidoOrderByDataEmissao(valorTotalPedido));
    }

    public List<ResponsePedidoDto> buscarPedidoPorTotalBetween(BigDecimal inicio, BigDecimal fim) {
        List<Pedido> pedidos;
        if (fim != null) {
            if (!(fim.compareTo(inicio) > 0)) {
                throw new IllegalArgumentException("O valor de inicio precisa ser maior que o do fim");
            }
            pedidos = pedidoRepository.findByTotalPedidoBetweenOrderByDataEmissao(inicio, fim);
        } else {
            pedidos = pedidoRepository.findByTotalPedidoBetweenOrderByDataEmissao(inicio, inicio);
        }
        return converteEntidadeParaDto(pedidos);
    }

    public List<ResponsePedidoDto> buscarPedidoPorStatus(StatusPedido status) {
        List<Pedido> pedidos = pedidoRepository.findByStatusPedidoOrderByDataEmissao(status);
        return converteEntidadeParaDto(pedidos);
    }

    private static void validarPedidoPertenceMesmoCaixa(Pedido pedido, Caixa caixaAtual) {
        if (!pedido.getCaixa().getId().equals(caixaAtual.getId())) {
            throw new ConflictTesourariaException("Edição não permitida: pedido pertence a outro caixa." +
                    " Recomendado criar outro pedido e efetuar sangria se necessário");
        }
    }

    private void atualizarEstoqueComDiferenca(List<ItemPedido> itensOriginais, List<ItemPedido> novosItens) {
        Map<Produto, BigDecimal> produtosOriginais = itensOriginais.stream()
                .collect(Collectors.groupingBy(
                        ItemPedido::getProduto,
                        Collectors.reducing(BigDecimal.ZERO, ItemPedido::getQuantidadeProduto, BigDecimal::add)
                ));

        Map<Produto, BigDecimal> produtosNovos = novosItens.stream()
                .collect(Collectors.groupingBy(
                        ItemPedido::getProduto,
                        Collectors.reducing(BigDecimal.ZERO, ItemPedido::getQuantidadeProduto, BigDecimal::add)
                ));

        Set<Produto> todosProdutos = new HashSet<>();
        todosProdutos.addAll(produtosOriginais.keySet());
        todosProdutos.addAll(produtosNovos.keySet());

        for (Produto produto : todosProdutos) {
            BigDecimal quantidadeOriginal = produtosOriginais.getOrDefault(produto, BigDecimal.ZERO);
            BigDecimal quantidadeNova = produtosNovos.getOrDefault(produto, BigDecimal.ZERO);
            BigDecimal diferenca = quantidadeNova.subtract(quantidadeOriginal);
            atualizarEstoqueIngrediente(produto, diferenca);
        }
    }

    private void atualizarEstoqueIngrediente(Produto produto, BigDecimal diferencaQuantidade) {
        produto.getIngredientesAssociados().forEach(ingredienteProduto -> {
            Ingrediente ingrediente = ingredienteRepository.findByCodigo(ingredienteProduto.getIngrediente().getCodigo())
                    .orElseThrow(() -> new EntityNotFoundException("Ingrediente não encontrado"));


            if (ingrediente.isControlarEstoque()) {
                BigDecimal quantidadeNecessaria = ingredienteProduto.getQuantidade().multiply(diferencaQuantidade);
                BigDecimal novoEstoque = ingrediente.getEstoque().subtract(quantidadeNecessaria);

                if (novoEstoque.compareTo(BigDecimal.ZERO) < 0) {
                    throw new EstoqueInsuficienteException(produto.getNome(), ingrediente.getNome(),
                            quantidadeNecessaria.abs(), ingrediente.getEstoque());
                }
                ingrediente.setEstoque(novoEstoque);
                ingredienteRepository.save(ingrediente);
            }
        });
    }

    private ResponsePedidoDto converteEntidadeParaDto(Pedido pedido) {
        return pedidoMapper.toResponse(pedido);
    }

    private List<ResponsePedidoDto> converteEntidadeParaDto(List<Pedido> pedidos) {
        return pedidos.stream()
                .map(this::converteEntidadeParaDto).toList();
    }

    private void processarItensDoPedido(RequestCriarPedidoDto dto, Pedido pedido, List<ItemPedido> itens) {
        dto.itens().forEach(i -> {
            Produto produto = produtoRepository.findByCodigoProduto(
                    i.codigoProduto()).orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

            diminuirEstoqueDoIngredienteRelacionadoAoProduto(produto, i.qtdProduto());
            ItemPedido item = new ItemPedido(i.qtdProduto(), pedido, produto);
            itens.add(item);
        });
    }

    private void processarItensDoPedido(RequestAtualizarPedidoDto dto, Pedido pedido, List<ItemPedido> itens) {
        dto.itens().forEach(i -> {
            Produto produto = produtoRepository.findByCodigoProduto(
                    i.codigoProduto()).orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

            diminuirEstoqueDoIngredienteRelacionadoAoProduto(produto, i.qtdProduto());
            ItemPedido item = new ItemPedido(i.qtdProduto(), pedido, produto);
            itens.add(item);
        });
    }

    private void diminuirEstoqueDoIngredienteRelacionadoAoProduto(Produto produto, BigDecimal quantidadeProduto) {
        produto.getIngredientesAssociados().forEach(i -> {
            Ingrediente ingrediente = ingredienteRepository.findByCodigo(i.getIngrediente().getCodigo())
                    .orElseThrow(() -> new EntityNotFoundException("Ingrediente não encontrado"));

            if (ingrediente.isControlarEstoque()) {
                BigDecimal quantidadeNecessaria = i.getQuantidade().multiply(quantidadeProduto);

                if (ingrediente.getEstoque().compareTo(quantidadeNecessaria) < 0) {
                    throw new EstoqueInsuficienteException(produto.getNome(),
                            ingrediente.getNome(), quantidadeNecessaria, ingrediente.getEstoque());
                }

                ingrediente.setEstoque(ingrediente.getEstoque().subtract(quantidadeNecessaria));
                ingredienteRepository.save(ingrediente);
            }
        });
    }

    private Caixa validarCaixaAberto(String usuario) {
        Optional<Caixa> caixa = caixaRepository.findByUsuarioUtilizandoAndDataFechamentoIsNull(usuario);
        if (caixa.isEmpty()) {
            throw new ConflictTesourariaException("Usuario não possui um caixa aberto.");
        } else {
            return caixa.get();
        }
    }
}
