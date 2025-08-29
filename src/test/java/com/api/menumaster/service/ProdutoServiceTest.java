package com.api.menumaster.service;

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
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

        IngredienteProduto ingredienteProduto = new IngredienteProduto();
        ingredienteProduto.setIngrediente(ingredienteSalvo);
        ingredienteProduto.setQuantidade(BigDecimal.valueOf(1));
        ingredienteProduto.setProduto(novoProduto);

        novoProduto.setIngredientesAssociados(List.of(ingredienteProduto));

        responseProdutoDto = new ResponseProdutoDto(
                novoProduto.getNome(),
                1L,
                novoProduto.getDescricao(),
                novoProduto.getPrecoCusto(), // precoCusto calculado
                novoProduto.getPrecoVenda(), // precoVenda calculado
                novoProduto.isAtivo(),
                List.of(new ResponseIngredienteProdutoDto("ingredienteTeste", BigDecimal.valueOf(1)))
        );
    }

    @Test
    void criarProduto_deveRetornarResponseProdutoDto_quandoSucesso() {
        when(produtoRepository.findByNomeIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(produtoRepository.findByCodigoProduto(anyLong())).thenReturn(Optional.empty());
        when(produtoRepository.save(any(Produto.class))).thenReturn(novoProduto);
        when(ingredienteRepository.findByCodigo(1)).thenReturn(Optional.of(ingredienteSalvo));
        when(produtoMapper.toResponse(any(Produto.class))).thenReturn(responseProdutoDto);

        responseProdutoDto = produtoService.criarProduto(criaProdutoDto);

        assertNotNull(responseProdutoDto,
                "response não deveria ser nula");
        assertEquals(responseProdutoDto.nome(), novoProduto.getNome(),
                "nome não coincide");
        assertEquals(responseProdutoDto.codigoProduto(), novoProduto.getCodigoProduto(),
                "codigo não coincide");
        assertEquals(responseProdutoDto.precoCusto(), novoProduto.getPrecoCusto(),
                "preço custo não coincide");
        assertEquals(responseProdutoDto.precoVenda(), novoProduto.getPrecoVenda(),
                "preço venda não coincide");
        assertEquals(responseProdutoDto.isAtivo(), novoProduto.isAtivo(),
                "produto deveria estar ativo");
        assertEquals(responseProdutoDto.ingredientes().size(), novoProduto.getIngredientesAssociados().size(),
                "tamanhos não coincidem");
        assertEquals(responseProdutoDto.ingredientes().getFirst().nomeIngrediente(),
                novoProduto.getIngredientesAssociados().getFirst().getIngrediente().getNome(),
                "nomes não coincidem");
        assertEquals(responseProdutoDto.ingredientes().getFirst().quantidade(),
                novoProduto.getIngredientesAssociados().getFirst().getQuantidade(),
                "quantidades não coincidem");

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
    void atualizarProduto() {
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