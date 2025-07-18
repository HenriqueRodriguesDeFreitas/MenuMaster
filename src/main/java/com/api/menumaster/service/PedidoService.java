package com.api.menumaster.service;

import com.api.menumaster.dtos.request.RequestAtualizarPedidoDto;
import com.api.menumaster.dtos.request.RequestCriarPedidoDto;
import com.api.menumaster.dtos.response.ResponseItemPedidoDto;
import com.api.menumaster.dtos.response.ResponsePedidoDto;
import com.api.menumaster.exception.custom.ConflictEntityException;
import com.api.menumaster.exception.custom.EntityNotFoundException;
import com.api.menumaster.exception.custom.EstoqueInsuficienteException;
import com.api.menumaster.model.Ingrediente;
import com.api.menumaster.model.ItemPedido;
import com.api.menumaster.model.Pedido;
import com.api.menumaster.model.Produto;
import com.api.menumaster.model.enums.StatusPedido;
import com.api.menumaster.repository.IngredienteRepository;
import com.api.menumaster.repository.ItemPedidoRepository;
import com.api.menumaster.repository.PedidoRepository;
import com.api.menumaster.repository.ProdutoRepository;
import jakarta.transaction.Transactional;
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
    private final ItemPedidoRepository itemPedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final IngredienteRepository ingredienteRepository;

    public PedidoService(PedidoRepository pedidoRepository, ItemPedidoRepository itemPedidoRepository,
                         ProdutoRepository produtoRepository,
                         IngredienteRepository ingredienteRepository) {
        this.pedidoRepository = pedidoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
        this.produtoRepository = produtoRepository;
        this.ingredienteRepository = ingredienteRepository;
    }

    @Transactional
    public ResponsePedidoDto criarPedido(RequestCriarPedidoDto dto) {
        Pedido pedido = new Pedido();
        pedido.setDataEmissao(LocalDateTime.now());
        pedido.setDataEdicao(LocalDateTime.now());
        pedido.setNomeCliente(dto.nomeCliente());
        pedido.setEndereco(dto.endereco());
        pedido.setContato(dto.contato());
        pedido.setObservacao(dto.observacao());
        pedido.setStatusPedido(StatusPedido.AGUARDANDO);
        if (dto.mesa() == null || dto.mesa() < 0) {
            pedido.setMesa(0);
        }

        List<ItemPedido> itens = new ArrayList<>();
        processarItensDoPedido(dto, pedido, itens);

        pedido.setItensAssociados(itens);
        pedido.ajustarQuantidadeParaUnidade();
        pedido.calcularTotalPedido();

        return converteEntidadeParaDto(pedidoRepository.save(pedido));
    }

    @Transactional
    public ResponsePedidoDto atualizarPedido(UUID idPedido, RequestAtualizarPedidoDto dto) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new ConflictEntityException("Pedido não encontrado"));

        List<ItemPedido> itensOriginais = new ArrayList<>(pedido.getItensAssociados());

        pedido.setDataEdicao(LocalDateTime.now());
        pedido.setNomeCliente(dto.nomeCliente());
        pedido.setEndereco(dto.endereco());
        pedido.setContato(dto.contato());
        pedido.setObservacao(dto.observacao());
        pedido.setStatusPedido(dto.status());
        if (dto.mesa() == null || dto.mesa() < 0) {
            pedido.setMesa(0);
        }


        List<ItemPedido> novosItens = new ArrayList<>();

        processarItensDoPedido(dto, pedido, novosItens);

        atualizarEstoqueComDiferenca(pedido, itensOriginais, novosItens);

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

    public List<ResponsePedidoDto> buscarPedidosPorDataEmissao(LocalDate dataInicio,
                                                               LocalDate dataFim) {
        LocalTime tempoPadrao = LocalTime.of(0, 0, 0);


        List<Pedido> pedidos = new ArrayList<>();


        LocalDateTime dataHoraPedidoInicio = LocalDateTime.of(dataInicio.getYear(), dataInicio.getMonth(),
                dataInicio.getDayOfMonth(), tempoPadrao.getHour(),
                tempoPadrao.getMinute(), tempoPadrao.getSecond());

        if (dataFim != null) {
            LocalDateTime dataHoraPedidoFim = LocalDateTime.of(dataFim.getYear(), dataFim.getMonth(),
                    dataFim.getDayOfMonth(), 23, 59, 59);

            pedidos = pedidoRepository.findByDataEmissaoBetween(dataHoraPedidoInicio, dataHoraPedidoFim);
        } else {

            LocalDateTime dataHoraPedidoFim = LocalDateTime.of(dataInicio.getYear(), dataInicio.getMonth(),
                    dataInicio.getDayOfMonth(), 23, 59, 59);
            pedidos = pedidoRepository.findByDataEmissaoBetween(dataHoraPedidoInicio, dataHoraPedidoFim);
        }

        return converteEntidadeParaDto(pedidos);
    }

    public List<ResponsePedidoDto> buscarPorMesa(Integer mesa){
        List<Pedido> pedidos = pedidoRepository.findByMesaOrderByDataEmissao(mesa);
        return converteEntidadeParaDto(pedidos);
    }

    public List<ResponsePedidoDto> buscarPedidoPorTotal(BigDecimal inicio, BigDecimal fim) {
        List<Pedido> pedidos;
        if (fim != null) {
            if (!(fim.compareTo(inicio) > 0)) {
                throw new IllegalArgumentException("O valor de inicio precisa ser maior que o do fim");
            }
            pedidos = pedidoRepository.findByTotalPedidoBetween(inicio, fim);
        } else {
            pedidos = pedidoRepository.findByTotalPedidoBetween(inicio, inicio);
        }
        return converteEntidadeParaDto(pedidos);
    }

    public List<ResponsePedidoDto> buscarPedidoPorStatus(StatusPedido status){
        List<Pedido> pedidos = pedidoRepository.findByStatusPedidoOrderByDataEmissao(status);
        return converteEntidadeParaDto(pedidos);
    }

    private void atualizarEstoqueComDiferenca(Pedido pedido, List<ItemPedido> itensOriginais, List<ItemPedido> novosItens) {
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
        List<ResponseItemPedidoDto> itens = pedido.getItensAssociados()
                .stream()
                .map(i -> new ResponseItemPedidoDto(i.getProduto().getNome(), i.getQuantidadeProduto(),
                        i.getProduto().getPrecoVenda())).toList();

        return new ResponsePedidoDto(pedido.getId(), pedido.getMesa(), pedido.getStatusPedido(), pedido.getDataEmissao(),
                pedido.getDataEdicao(), pedido.getNomeCliente(), pedido.getEndereco(), pedido.getContato(),
                pedido.getObservacao(), pedido.getTotalPedido(), itens);
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

}
