package com.api.menumaster.service;

import com.api.menumaster.dtos.request.RequestAtualizarProdutoDto;
import com.api.menumaster.dtos.request.RequestCriaProdutoDto;
import com.api.menumaster.dtos.response.ResponseIngredienteProdutoDto;
import com.api.menumaster.dtos.response.ResponseProdutoDto;
import com.api.menumaster.exception.custom.ConflictEntityException;
import com.api.menumaster.exception.custom.EntityNotFoundException;
import com.api.menumaster.exception.custom.ProdutoVinculadoAPedidoException;
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

    public ProdutoService(ProdutoRepository produtoRepository,
                          IngredienteRepository ingredienteRepository) {
        this.produtoRepository = produtoRepository;
        this.ingredienteRepository = ingredienteRepository;
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
            if(!p.getNome().equals(produto.getNome())){
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

    public List<ResponseProdutoDto> findByPrecoCustoBetween(BigDecimal precoCustoInicial, BigDecimal precoCustoFinal) {
        List<Produto> produtos = new ArrayList<>();
        if (precoCustoInicial == null || precoCustoInicial.compareTo(BigDecimal.ZERO) < 0) {
            precoCustoInicial = BigDecimal.ZERO;
        }
        if (precoCustoFinal == null || precoCustoFinal.compareTo(BigDecimal.ZERO) < 0) {
            precoCustoFinal = BigDecimal.ZERO;
        }

        if (precoCustoInicial.compareTo(BigDecimal.ZERO) == 0 && precoCustoFinal.compareTo(BigDecimal.ZERO) >= 0) {
            produtos = produtoRepository.findByPrecoCustoBetween(BigDecimal.ZERO, precoCustoFinal);
        }
        if (precoCustoInicial.compareTo(BigDecimal.ZERO) >= 0 && precoCustoFinal.compareTo(BigDecimal.ZERO) == 0) {
            produtos = produtoRepository.findByPrecoCusto(precoCustoInicial);
        }


        return converterObjetoParaDto(produtos);
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
        List<ResponseIngredienteProdutoDto> ingredientesDoProduto = produto.getIngredientesAssociados().stream()
                .map(i -> new ResponseIngredienteProdutoDto(i.getIngrediente().getNome(), i.getQuantidade())).toList();

        return new ResponseProdutoDto(produto.getNome(), produto.getCodigoProduto(), produto.getDescricao(),
                produto.getPrecoCusto(), produto.getPrecoVenda(), produto.isAtivo(), ingredientesDoProduto);
    }

    private List<ResponseProdutoDto> converterObjetoParaDto(List<Produto> produtos) {
        return produtos.stream().map(this::converterObjetoParaDto).toList();
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
}
