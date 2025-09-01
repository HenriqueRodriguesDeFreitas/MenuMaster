package com.api.menumaster.service;

import com.api.menumaster.dtos.request.RequestAtualizarProdutoDto;
import com.api.menumaster.dtos.request.RequestCriaProdutoDto;
import com.api.menumaster.dtos.response.ResponseProdutoDto;
import com.api.menumaster.exception.custom.ConflictEntityException;
import com.api.menumaster.exception.custom.EntityNotFoundException;
import com.api.menumaster.exception.custom.ProdutoVinculadoAPedidoException;
import com.api.menumaster.mappper.ProdutoMapper;
import com.api.menumaster.model.Ingrediente;
import com.api.menumaster.model.IngredienteProduto;
import com.api.menumaster.model.Produto;
import com.api.menumaster.repository.IngredienteRepository;
import com.api.menumaster.repository.ProdutoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final IngredienteRepository ingredienteRepository;
    private final ProdutoMapper produtoMapper;

    public ProdutoService(ProdutoRepository produtoRepository,
                          IngredienteRepository ingredienteRepository,
                          ProdutoMapper produtoMapper) {
        this.produtoRepository = produtoRepository;
        this.ingredienteRepository = ingredienteRepository;
        this.produtoMapper = produtoMapper;
    }

    @Transactional
    public ResponseProdutoDto criarProduto(RequestCriaProdutoDto dto) {
        produtoRepository.findByNomeIgnoreCase(dto.nome()).ifPresent(p -> {
            throw new ConflictEntityException("Já existe um produto com este nome");
        });
        produtoRepository.findByCodigoProduto(dto.codigoProduto()).ifPresent(p -> {
            throw new ConflictEntityException("Já existe um produto com este codigo");
        });

        Produto produto = new Produto();
        produto.setCodigoProduto(dto.codigoProduto());
        produto.setNome(dto.nome());
        produto.setDescricao(dto.descricao());
        produto.setAtivo(true);
        produto.setQuantidadeVendida(BigDecimal.ZERO);
        produto.setUnidadeMedida(dto.unidadeMedida());

        List<IngredienteProduto> ingredienteProdutos = new ArrayList<>();

        processarIngredientesDoProduto(dto, produto, ingredienteProdutos);

        produto.setIngredientesAssociados(ingredienteProdutos);
        produto.calcularPrecoCusto();
        produto.calcularPrecoVenda();

        var response = produtoRepository.save(produto);
        return converterObjetoParaDto(response);
    }

    @Transactional
    public ResponseProdutoDto atualizarProduto(Long codigoProduto, RequestAtualizarProdutoDto dto) {
        Produto produto = produtoRepository.findByCodigoProduto(codigoProduto)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        produtoRepository.findByNomeIgnoreCase(dto.nome()).ifPresent(p -> {
            if (!p.getNome().equals(produto.getNome())) {
                throw new ConflictEntityException("Já existe um produto com este nome");
            }
        });

        produto.setNome(dto.nome());
        produto.setDescricao(dto.descricao());
        produto.setAtivo(dto.isAtivo());
        produto.getIngredientesAssociados().clear();

        processarIngredientesDoProduto(dto, produto, produto.getIngredientesAssociados());

        produto.calcularPrecoCusto();
        produto.calcularPrecoVenda();
        return converterObjetoParaDto(produtoRepository.save(produto));
    }

    public List<ResponseProdutoDto> findAll() {
        List<Produto> produtos = produtoRepository.findAll();
        return converterObjetoParaDto(produtos);
    }

    public ResponseProdutoDto findByCodigo(Long codigo) {
        return converterObjetoParaDto(produtoRepository.findByCodigoProduto(codigo)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado")));
    }

    public List<ResponseProdutoDto> findByNome(String nome) {
        List<Produto> produtos = produtoRepository.findByNomeIgnoreCaseContaining(nome);
        return converterObjetoParaDto(produtos);
    }

    public List<ResponseProdutoDto> findByPrecoCusto(BigDecimal precoCusto) {
        if (precoCusto == null) {
            throw new IllegalArgumentException("O valor de preço de custo não pode ser nulo.");
        }
        if (precoCusto.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O valor de preço de custo não pode ser negativo.");
        }
        return converterObjetoParaDto(produtoRepository.findByPrecoCusto(precoCusto));

    }

    public List<ResponseProdutoDto> findByPrecoCustoBetween(BigDecimal precoCustoInicial, BigDecimal precoCustoFinal) {
        if (precoCustoInicial == null) throw new IllegalArgumentException("Preço inicial não pode ser nulo.");
        if (precoCustoFinal == null) throw new IllegalArgumentException("Preço final não pode ser nulo.");

        if (precoCustoInicial.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Preço inicial não pode ser negativo.");

        if (precoCustoFinal.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Preço final não pode ser negativo.");

        if (precoCustoInicial.compareTo(precoCustoFinal) > 0)
            throw new IllegalArgumentException("Preço inicial não pode ser maior que o preço final.");

        return converterObjetoParaDto(produtoRepository.findByPrecoCustoBetween(precoCustoInicial, precoCustoFinal));
    }

    public List<ResponseProdutoDto> findByPrecoVendaBetween(BigDecimal precoVendaInicial, BigDecimal precoVendaFinal) {
        List<Produto> produtos = new ArrayList<>();
        if (precoVendaInicial == null || precoVendaInicial.compareTo(BigDecimal.ZERO) < 0) {
            precoVendaInicial = BigDecimal.ZERO;
        }
        if (precoVendaFinal == null || precoVendaFinal.compareTo(BigDecimal.ZERO) < 0) {
            precoVendaFinal = BigDecimal.ZERO;
        }

        if (precoVendaInicial.compareTo(BigDecimal.ZERO) == 0 && precoVendaFinal.compareTo(BigDecimal.ZERO) > 0) {
            produtos = produtoRepository.findByPrecoVendaBetween(BigDecimal.ZERO, precoVendaFinal);
        }
        if (precoVendaInicial.compareTo(BigDecimal.ZERO) >= 0 && precoVendaFinal.compareTo(BigDecimal.ZERO) == 0) {
            produtos = produtoRepository.findByPrecoVenda(precoVendaInicial);
        }

        return converterObjetoParaDto(produtos);
    }

    public List<ResponseProdutoDto> findByProdutosAtivos() {
        return converterObjetoParaDto(produtoRepository.findByIsAtivoTrue());
    }

    public List<ResponseProdutoDto> findByProdutosInativos() {
        return converterObjetoParaDto(produtoRepository.findByIsAtivoFalse());
    }

    public void deleteByCodigo(Long codigoProduto) {
        Produto produto = produtoRepository.findByCodigoProduto(codigoProduto)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
        if (!(produto.getItensProduto() == null || produto.getItensProduto().isEmpty())) {
            throw new ProdutoVinculadoAPedidoException("Produto: " + produto.getNome() + " impossibilatado para deletar, recomenda-se desativalo");
        }
        produtoRepository.deleteById(produto.getId());
    }

    private ResponseProdutoDto converterObjetoParaDto(Produto produto) {
        return produtoMapper.toResponse(produto);
    }

    private List<ResponseProdutoDto> converterObjetoParaDto(List<Produto> produtos) {
        List<ResponseProdutoDto> response = new ArrayList<>();
        produtos.forEach(i -> response.add(produtoMapper.toResponse(i)));
        return response;
    }

    private void processarIngredientesDoProduto(RequestCriaProdutoDto dto, Produto produto,
                                                List<IngredienteProduto> ingredienteProdutos) {
        dto.ingredientes().forEach(i -> {
            Ingrediente ingrediente = ingredienteRepository.findByCodigo(i.codigoIngrediente())
                    .orElseThrow(() -> new EntityNotFoundException("Ingrediente não encontrado"));

            if (!ingrediente.isAtivo()) {
                throw new ConflictEntityException("Ingrediente: " + ingrediente.getNome() + " se encontra inativo");
            }

            IngredienteProduto ingredienteProduto = new IngredienteProduto();
            ingredienteProduto.setProduto(produto);
            ingredienteProduto.setIngrediente(ingrediente);
            ingredienteProduto.setQuantidade(i.quantidade());
            ingredienteProdutos.add(ingredienteProduto);
        });
    }

    private void processarIngredientesDoProduto(RequestAtualizarProdutoDto dto, Produto produto,
                                                List<IngredienteProduto> listaExistente) {
        dto.ingredientes().forEach(i -> {
            Ingrediente ingrediente = ingredienteRepository.findByCodigo(i.codigoIngrediente())
                    .orElseThrow(() -> new EntityNotFoundException("Ingrediente não encontrado"));

            if (!ingrediente.isAtivo()) {
                throw new ConflictEntityException("Ingrediente: " + ingrediente.getNome() + " se encontra inativo");
            }

            IngredienteProduto ingredienteProduto = new IngredienteProduto();
            ingredienteProduto.setProduto(produto);
            ingredienteProduto.setIngrediente(ingrediente);
            ingredienteProduto.setQuantidade(i.quantidade());
            listaExistente.add(ingredienteProduto);
        });
    }

    private static void validaSeValoresSaoPositivos(BigDecimal valorInicial,
                                                    BigDecimal valorFinal) {
        if (valorInicial != null && valorInicial.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Valor inicial não pode ser negativo.");

        if (valorFinal != null && valorFinal.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Valor final não pode ser negativo.");
    }

    private static void validaSeValorInicialMenorQueFinal(BigDecimal valorInicial,
                                                          BigDecimal valorFinal) {
        if (valorInicial.compareTo(valorFinal) > 0)
            throw new IllegalArgumentException("Valor inicial não pode ser maior que o preço final.");
    }
}
