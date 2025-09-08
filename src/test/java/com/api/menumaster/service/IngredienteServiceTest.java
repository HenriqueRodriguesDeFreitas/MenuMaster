package com.api.menumaster.service;

import com.api.menumaster.dtos.request.RequestIngredienteDto;
import com.api.menumaster.dtos.response.ResponseIngredienteDto;
import com.api.menumaster.mappper.IngredienteMapper;
import com.api.menumaster.model.Ingrediente;
import com.api.menumaster.model.enums.UnidadeMedida;
import com.api.menumaster.repository.IngredienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngredienteServiceTest {

    @Mock
    private IngredienteRepository ingredienteRepository;
    @Mock
    private IngredienteMapper ingredienteMapper;
    @InjectMocks
    private IngredienteService ingredienteService;

    private Ingrediente ingredienteSalvo;
    private RequestIngredienteDto requestIngredienteDto;

    @BeforeEach
    void setUp() {
        when(ingredienteMapper.toResponse(any(Ingrediente.class))).thenAnswer(invocation -> {
            Ingrediente i = invocation.getArgument(0);
            return new ResponseIngredienteDto(i.getId(),
                    i.getCodigo(),
                    i.getNome(),
                    i.getDescricao(),
                    i.getEstoque(),
                    i.getPrecoCusto(),
                    i.getPrecoVenda(),
                    i.isAtivo(),
                    i.isAdicional(),
                    i.getUnidadeMedida(),
                    i.isControlarEstoque());
        });

        requestIngredienteDto = new RequestIngredienteDto(
                1,
                "Ingrediente Teste",
                "Descrição do ingrediente teste",
                BigDecimal.valueOf(10.0),
                BigDecimal.valueOf(20.0),
                false,
                UnidadeMedida.UN,
                false
        );

        ingredienteSalvo = new Ingrediente();
        ingredienteSalvo.setId(UUID.randomUUID());
        ingredienteSalvo.setCodigo(1);
        ingredienteSalvo.setNome("Ingrediente Teste");
        ingredienteSalvo.setDescricao("Descrição do ingrediente teste");
        ingredienteSalvo.setPrecoCusto(BigDecimal.valueOf(10));
        ingredienteSalvo.setPrecoVenda(BigDecimal.valueOf(20));
        ingredienteSalvo.setAdicional(false);
        ingredienteSalvo.setUnidadeMedida(UnidadeMedida.UN);
        ingredienteSalvo.setControlarEstoque(false);
    }

    @Test
    void salvarNovoIngrediente() {
        when(ingredienteRepository.findByCodigo(anyInt())).thenReturn(Optional.empty());
        when(ingredienteRepository.findByNomeIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(ingredienteRepository.save(any(Ingrediente.class))).thenReturn(ingredienteSalvo);


        ResponseIngredienteDto resultado = ingredienteService.salvarNovoIngrediente(requestIngredienteDto);
        assertNotNull(resultado, "O retorno não deveria ser nulo");
        assertEquals(resultado.id(), ingredienteSalvo.getId(), "Id não coincide.");
        assertEquals(resultado.nome(), ingredienteSalvo.getNome(), "Nome não coincide.");
        assertEquals(resultado.descricao(), ingredienteSalvo.getDescricao(), "Descrição não coincide.");
        assertEquals(resultado.estoque(), ingredienteSalvo.getEstoque(), "Estoque não coincide.");
        assertEquals(resultado.precoCusto(), ingredienteSalvo.getPrecoCusto(), "Preço custo não coincide.");
        assertEquals(resultado.precoVenda(), ingredienteSalvo.getPrecoVenda(), "Preço venda não coincide.");
        assertEquals(resultado.isAtivo(), ingredienteSalvo.isAtivo(), "isAtivo não coincide.");
        assertEquals(resultado.isAdicional(), ingredienteSalvo.isAdicional(), "isAdicional não coincide.");
        assertEquals(resultado.unidadeMedida(), ingredienteSalvo.getUnidadeMedida(), "Unidade de medida não coincide.");
        assertEquals(resultado.controlarEstoque(), ingredienteSalvo.isControlarEstoque(), "isControlar estoque não coincide.");

        verify(ingredienteRepository, times(1)).findByCodigo(1);
        verify(ingredienteRepository, times(1)).findByNomeIgnoreCase(anyString());
        verify(ingredienteRepository, times(1)).save(any(Ingrediente.class));
        verifyNoMoreInteractions(ingredienteRepository);
        verifyNoMoreInteractions(ingredienteMapper);

    }

    @Test
    void atualizarIngrediente() {
    }

    @Test
    void deleteById() {
    }
}