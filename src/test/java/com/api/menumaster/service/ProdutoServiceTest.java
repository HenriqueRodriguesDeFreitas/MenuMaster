package com.api.menumaster.service;

import com.api.menumaster.dtos.request.RequestAtualizarProdutoDto;
import com.api.menumaster.dtos.request.RequestCriaProdutoDto;
import com.api.menumaster.dtos.request.RequestIngredienteProdutoDto;
import com.api.menumaster.dtos.response.ResponseIngredienteProdutoDto;
import com.api.menumaster.dtos.response.ResponseProdutoDto;
import com.api.menumaster.exception.custom.ConflictEntityException;
import com.api.menumaster.exception.custom.EntityNotFoundException;
import com.api.menumaster.mappper.ProdutoMapper;
import com.api.menumaster.model.Ingrediente;
import com.api.menumaster.model.IngredienteProduto;
import com.api.menumaster.model.Produto;
import com.api.menumaster.model.enums.UnidadeMedida;
import com.api.menumaster.repository.IngredienteRepository;
import com.api.menumaster.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private IngredienteRepository ingredienteRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private ProdutoMapper produtoMapper;

    @InjectMocks
    private ProdutoService produtoService;

    private RequestCriaProdutoDto criaProdutoDto;
    private ResponseProdutoDto responseProdutoDto;
    private RequestIngredienteProdutoDto ingredienteProdutoDto;
    private Produto novoProduto;
    private Ingrediente ingredienteSalvo;
    IngredienteProduto ingredienteProduto;
    RequestAtualizarProdutoDto requestAtualizarProdutoDto;

    @BeforeEach
    void setUp() {
        ingredienteProdutoDto = new RequestIngredienteProdutoDto(1, BigDecimal.valueOf(1));

        criaProdutoDto = new RequestCriaProdutoDto(1L, "produtoTeste", "descricaoTeste",
                UnidadeMedida.UN, List.of(ingredienteProdutoDto));

        ingredienteSalvo = new Ingrediente("ingredienteTeste", "ingredienteTeste",
                BigDecimal.valueOf(10), BigDecimal.valueOf(10),
                BigDecimal.valueOf(15), true,
                false, UnidadeMedida.UN, false);
        ingredienteSalvo.setCodigo(1);


        novoProduto = new Produto();
        novoProduto.setId(UUID.randomUUID());
        novoProduto.setCodigoProduto(criaProdutoDto.codigoProduto());
        novoProduto.setNome(criaProdutoDto.nome());
        novoProduto.setDescricao(criaProdutoDto.descricao());
        novoProduto.setAtivo(true);
        novoProduto.setQuantidadeVendida(BigDecimal.ZERO);
        novoProduto.setUnidadeMedida(criaProdutoDto.unidadeMedida());

        ingredienteProduto = new IngredienteProduto();
        ingredienteProduto.setIngrediente(ingredienteSalvo);
        ingredienteProduto.setQuantidade(BigDecimal.valueOf(1));
        ingredienteProduto.setProduto(novoProduto);

        novoProduto.setIngredientesAssociados(new ArrayList<>());
        novoProduto.getIngredientesAssociados().add(ingredienteProduto);


        responseProdutoDto = new ResponseProdutoDto(
                novoProduto.getNome(),
                1L,
                novoProduto.getDescricao(),
                novoProduto.getPrecoCusto(), // precoCusto calculado
                novoProduto.getPrecoVenda(), // precoVenda calculado
                novoProduto.isAtivo(),
                List.of(new ResponseIngredienteProdutoDto("ingredienteTeste", BigDecimal.valueOf(1)))
        );

        requestAtualizarProdutoDto = new RequestAtualizarProdutoDto(
                "novoNome",
                "novaDescricao",
                false,
                List.of(ingredienteProdutoDto));
    }

    @Test
    void criarProduto_deveRetornarResponseProdutoDto_quandoSucesso() {
        when(produtoRepository.findByNomeIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(produtoRepository.findByCodigoProduto(anyLong())).thenReturn(Optional.empty());
        when(produtoRepository.save(any(Produto.class))).thenReturn(novoProduto);
        when(ingredienteRepository.findByCodigo(1)).thenReturn(Optional.of(ingredienteSalvo));
        when(produtoMapper.toResponse(any(Produto.class))).thenReturn(responseProdutoDto);

        responseProdutoDto = produtoService.criarProduto(criaProdutoDto);

        verificacaoDeAsserts(responseProdutoDto, novoProduto);

        verify(produtoRepository, times(1)).findByNomeIgnoreCase(anyString());
        verify(produtoRepository, times(1)).findByCodigoProduto(anyLong());
        verify(ingredienteRepository, times(1)).findByCodigo(anyInt());
        verify(produtoRepository, times(1)).save(any(Produto.class));
        verifyNoMoreInteractions(produtoRepository);
        verifyNoMoreInteractions(ingredienteRepository);
    }

    @Test
    void criarProduto_deveRetornarConflictEntityException_quandoExisteProdutoComNomePassado() {
        when(produtoRepository.findByNomeIgnoreCase("produtoTeste")).thenReturn(Optional.of(novoProduto));

        ConflictEntityException exception = assertThrows(ConflictEntityException.class, () -> produtoService.criarProduto(criaProdutoDto));

        assertEquals("Já existe um produto com este nome", exception.getMessage());
        verify(produtoRepository, times(1)).findByNomeIgnoreCase(criaProdutoDto.nome());
        verifyNoMoreInteractions(produtoRepository);
    }

    @Test
    void criarProduto_deveRetornarConflictEntityException_quandoExisteProdutoComCodigoPassado() {
        when(produtoRepository.findByNomeIgnoreCase("produtoTeste")).thenReturn(Optional.empty());
        when(produtoRepository.findByCodigoProduto(1L)).thenReturn(Optional.of(novoProduto));

        ConflictEntityException exception = assertThrows(ConflictEntityException.class, () -> produtoService.criarProduto(criaProdutoDto));

        assertEquals("Já existe um produto com este codigo", exception.getMessage());
        verify(produtoRepository).findByCodigoProduto(criaProdutoDto.codigoProduto());
        verifyNoMoreInteractions(produtoRepository);
    }

    @Test
    void criarProduto_deveRetornarEntityNotFoundException_quandoIngredienteAssociadoNaoEncontrado() {
        when(produtoRepository.findByNomeIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(produtoRepository.findByCodigoProduto(anyLong())).thenReturn(Optional.empty());
        when(ingredienteRepository.findByCodigo(ingredienteProdutoDto.codigoIngrediente())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> produtoService.criarProduto(criaProdutoDto));

        assertEquals("Ingrediente não encontrado", exception.getMessage(), "mensagem de erro não coincide");
        verify(produtoRepository, times(1)).findByNomeIgnoreCase(anyString());
        verify(produtoRepository, times(1)).findByCodigoProduto(anyLong());
        verify(ingredienteRepository, times(1)).findByCodigo(anyInt());
        verifyNoMoreInteractions(produtoRepository);
        verifyNoMoreInteractions(ingredienteRepository);
    }

    @Test
    void criarProduto_deveRetornarConflictEntityException_quandoIngredienteAssociadoInativo() {
        when(produtoRepository.findByNomeIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(produtoRepository.findByCodigoProduto(anyLong())).thenReturn(Optional.empty());
        when(ingredienteRepository.findByCodigo(1)).thenReturn(Optional.of(ingredienteSalvo));

        ingredienteSalvo.setAtivo(false);

        ConflictEntityException exception = assertThrows(ConflictEntityException.class, () -> produtoService.criarProduto(criaProdutoDto));

        assertEquals("Ingrediente: " + ingredienteSalvo.getNome() + " se encontra inativo",
                exception.getMessage(), "mensagem de erro não coincide");
        verify(produtoRepository, times(1)).findByNomeIgnoreCase(anyString());
        verify(produtoRepository, times(1)).findByCodigoProduto(anyLong());
        verify(ingredienteRepository, times(1)).findByCodigo(anyInt());
        verifyNoMoreInteractions(produtoRepository);
        verifyNoMoreInteractions(ingredienteRepository);
    }

    @Test
    void atualizarProduto_deveRetornarResponseProdutoDto_quandoSucesso() {
        when(produtoRepository.findByCodigoProduto(1L)).thenReturn(Optional.of(novoProduto));
        when(produtoRepository.findByNomeIgnoreCase("novoNome")).thenReturn(Optional.empty());
        when(ingredienteRepository.findByCodigo(1)).thenReturn(Optional.of(ingredienteSalvo));
        when(produtoRepository.save(any(Produto.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(produtoMapper.toResponse(any(Produto.class))).thenAnswer(invocation -> {
            Produto p = invocation.getArgument(0);
            return responseProdutoDtoComDadosAtualizados(p);
        });

        ResponseProdutoDto response = produtoService.atualizarProduto(1L, requestAtualizarProdutoDto);


        novoProduto.setNome("novoNome");
        novoProduto.setDescricao("novaDescricao");
        novoProduto.setAtivo(false);

        verificacaoDeAsserts(response, novoProduto);

        verify(produtoRepository, times(1)).findByCodigoProduto(1L);
        verify(produtoRepository, times(1)).findByNomeIgnoreCase("novoNome");
        verify(ingredienteRepository, times(1)).findByCodigo(1);
        verify(produtoRepository, times(1)).save(any(Produto.class));
        verify(produtoMapper, times(1)).toResponse(any(Produto.class));
        verifyNoMoreInteractions(produtoRepository, ingredienteRepository, produtoMapper);
    }

    @Test
    void atualizarProduto_deveRetornarEntityNotFoundException_quandoProdutoNaoEncontradoComCodigoPassado() {
        when(produtoRepository.findByCodigoProduto(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> produtoService.atualizarProduto(1L, requestAtualizarProdutoDto));

        assertEquals("Produto não encontrado", exception.getMessage(), "mensagem não coincide");
        verify(produtoRepository, times(1)).findByCodigoProduto(1L);
        verifyNoMoreInteractions(produtoRepository);
    }

    @Test
    void atualizarProduto_deveRetornarConflictEntityException_quandoExisteProdutoComNomePassado() {
        Produto produtoCadastrado = new Produto();
        produtoCadastrado.setId(UUID.randomUUID());
        produtoCadastrado.setCodigoProduto(criaProdutoDto.codigoProduto());
        produtoCadastrado.setNome("novoNome");
        produtoCadastrado.setDescricao(criaProdutoDto.descricao());
        produtoCadastrado.setAtivo(true);
        produtoCadastrado.setQuantidadeVendida(BigDecimal.ZERO);
        produtoCadastrado.setUnidadeMedida(criaProdutoDto.unidadeMedida());

        when(produtoRepository.findByCodigoProduto(1L)).thenReturn(Optional.of(novoProduto));
        when(produtoRepository.findByNomeIgnoreCase("novoNome")).thenReturn(
                Optional.of(produtoCadastrado));

        ConflictEntityException exception = assertThrows(ConflictEntityException.class,
                () -> produtoService.atualizarProduto(1L, requestAtualizarProdutoDto));

        assertEquals("Já existe um produto com este nome", exception.getMessage(),
                "mensagem não coincide");
        verify(produtoRepository, times(1)).findByCodigoProduto(1L);
        verify(produtoRepository, times(1)).findByNomeIgnoreCase("novoNome");
        verifyNoMoreInteractions(produtoRepository);
    }

    @Test
    void atualizarProduto_deveRetornarEntityNotFoundException_quandoIngredienteAssociadoNaoEncontrado() {
        when(produtoRepository.findByCodigoProduto(1L)).thenReturn(Optional.of(novoProduto));
        when(produtoRepository.findByNomeIgnoreCase("novoNome")).thenReturn(Optional.empty());
        when(ingredienteRepository.findByCodigo(1)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> produtoService.atualizarProduto(1L, requestAtualizarProdutoDto));

        assertEquals("Ingrediente não encontrado", exception.getMessage(), "mensagem não coincide");
        verify(produtoRepository, times(1)).findByCodigoProduto(1L);
        verify(produtoRepository, times(1)).findByNomeIgnoreCase("novoNome");
        verify(ingredienteRepository, times(1)).findByCodigo(1);
        verifyNoMoreInteractions(produtoRepository);
        verifyNoMoreInteractions(ingredienteRepository);
    }

    @Test
    void atualizarProduto_deveRetornarConflictEntityException_quandoIngredienteAssociadoInativo(){
        when(produtoRepository.findByCodigoProduto(1L)).thenReturn(Optional.of(novoProduto));
        when(produtoRepository.findByNomeIgnoreCase("novoNome")).thenReturn(Optional.empty());
        when(ingredienteRepository.findByCodigo(1)).thenReturn(Optional.of(ingredienteSalvo));

        ingredienteSalvo.setAtivo(false);

        ConflictEntityException exception = assertThrows(ConflictEntityException.class,
                ()-> produtoService.atualizarProduto(1L, requestAtualizarProdutoDto));

        assertEquals("Ingrediente: " + ingredienteSalvo.getNome() + " se encontra inativo", exception.getMessage(),
                 "mensagem não coincide");
        verify(produtoRepository, times(1)).findByCodigoProduto(1L);
        verify(produtoRepository, times(1)).findByNomeIgnoreCase("novoNome");
        verify(ingredienteRepository, times(1)).findByCodigo(1);
        verifyNoMoreInteractions(produtoRepository);
        verifyNoMoreInteractions(ingredienteRepository);
    }

    @Test
    void findAll_deveRetornarListaDeResponseProdutoDto_quandoSucesso() {
        List<Produto> produtos = List.of(novoProduto);
        when(produtoRepository.findAll()).thenReturn(produtos);
        when(produtoMapper.toResponse(any(Produto.class))).thenReturn(responseProdutoDto);

        List<ResponseProdutoDto> responses;
        responses = produtoService.findAll();

        verificacaoDeAssertsList(responses, produtos);
        verify(produtoRepository, times(1)).findAll();
        verify(produtoMapper, times(1)).toResponse(any(Produto.class));
        verifyNoMoreInteractions(produtoRepository);
        verifyNoMoreInteractions(produtoMapper);
    }

    @Test
    void findAll_deveRetornarListVazia_quandoNenhumProdutoEncontrado() {
        when(produtoRepository.findAll()).thenReturn(List.of());

        List<ResponseProdutoDto> responses = produtoService.findAll();

        assertTrue(responses.isEmpty(), "retorno deveria ser nulo");
    }

    @Test
    void findByCodigo() {
    }

    @Test
    void findByNome() {
    }

    @Test
    void findByPrecoCustoBetween() {
    }

    @Test
    void findByPrecoVendaBetween() {
    }

    @Test
    void findByProdutosAtivos() {
    }

    @Test
    void findByProdutosInativos() {
    }

    @Test
    void deleteByCodigo() {
    }
}