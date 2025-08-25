package com.api.menumaster.service;

import com.api.menumaster.dtos.request.RequestMovimentoTesourariaDto;
import com.api.menumaster.dtos.response.ResponseTesourariaMovimentoDto;
import com.api.menumaster.exception.custom.ConflictTesourariaException;
import com.api.menumaster.mappper.TesourariaMovimentoMapper;
import com.api.menumaster.model.Tesouraria;
import com.api.menumaster.model.TesourariaMovimento;
import com.api.menumaster.model.enums.FormaPagamento;
import com.api.menumaster.model.enums.TipoMovimento;
import com.api.menumaster.repository.TesourariaMovimentacaoRepository;
import com.api.menumaster.repository.TesourariaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TesourariaMovimentoServiceTest {

    @Mock
    private TesourariaRepository tesourariaRepository;
    @Mock
    private TesourariaMovimentacaoRepository movimentoRepository;
    @Mock
    private Authentication authentication;
    @Mock
    private TesourariaMovimentoMapper movimentoMapper;
    @InjectMocks
    private TesourariaMovimentoService movimentacaoService;

    private Tesouraria tesourariaAberta;
    private TesourariaMovimento movimentoSalvo;
    private RequestMovimentoTesourariaDto requestMovimentoDto;
    private ResponseTesourariaMovimentoDto responseMovimentoDto;

    @BeforeEach
    void setUp() {
        tesourariaAberta = new Tesouraria(BigDecimal.valueOf(100), authentication.getName());
        tesourariaAberta.setDataAbertura(LocalDateTime.now());
        tesourariaAberta.setId(UUID.randomUUID());

        requestMovimentoDto = new RequestMovimentoTesourariaDto(FormaPagamento.DINHEIRO, BigDecimal.valueOf(10),
                "teste");

        movimentoSalvo = new TesourariaMovimento(tesourariaAberta, LocalDateTime.now(),
                TipoMovimento.ENTRADA, FormaPagamento.DINHEIRO, BigDecimal.valueOf(10),
                "usuarioTeste", "teste");
        movimentoSalvo.setId(UUID.randomUUID());

        // instanciando DTO esperado para não vir null
        responseMovimentoDto = new ResponseTesourariaMovimentoDto(
                movimentoSalvo.getId(),
                movimentoSalvo.getDataMovimentacao(),
                TipoMovimento.ENTRADA.name(),
                FormaPagamento.DINHEIRO.name(),
                BigDecimal.valueOf(10),
                "teste",
                "usuarioTeste"
        );
    }

    @Test
    void efetuarMovimentoEntrada_deveRetornarResponseTesourariaMovimentoDto_quandoSucesso() {

        when(tesourariaRepository.findByDataFechamentoIsNull()).thenReturn(Optional.of(tesourariaAberta));
        when(authentication.getName()).thenReturn("usuarioTeste");
        when(movimentoRepository.save(any(TesourariaMovimento.class))).thenReturn(movimentoSalvo);
        when(movimentoMapper.toResponse(any(TesourariaMovimento.class))).thenReturn(responseMovimentoDto);

        ResponseTesourariaMovimentoDto resultado =
                movimentacaoService.efetuarMovimentoEntrada(requestMovimentoDto, authentication);

        assertNotNull(resultado, "não deveria ser nulo.");
        assertEquals(movimentoSalvo.getId(), resultado.idMovimento(), "id não coincide");
        assertEquals(movimentoSalvo.getDataMovimentacao(), resultado.dataMovimento(), "data não coincide");
        assertEquals(movimentoSalvo.getTipoMovimento().name(), resultado.tipoMovimento(), "tipo de movimento não coincide");
        assertEquals(movimentoSalvo.getFormaPagamento().name(), resultado.formaPagamento(), "forma de pagamento não coincide");
        assertEquals(movimentoSalvo.getValor(), resultado.valor(), "valor não coincide");
        assertEquals(movimentoSalvo.getValor(), resultado.valor(), "valor não coincide");
        assertEquals(movimentoSalvo.getDescricao(), resultado.descricao(), "descrição não coincide");
        assertEquals(movimentoSalvo.getUsuario(), resultado.usuarioMovimento(), "nome de usuario não coincide");

        verify(tesourariaRepository, times(1)).findByDataFechamentoIsNull();
        verify(movimentoRepository, times(1)).save(any(TesourariaMovimento.class));
        verify(tesourariaRepository, times(1)).save(tesourariaAberta);
        verify(movimentoMapper, times(1)).toResponse(movimentoSalvo);
        verifyNoMoreInteractions(tesourariaRepository);
        verifyNoMoreInteractions(movimentoRepository);
    }

    @Test
    void efetuarMovimentoEntrada_deveRetornarConflictTesourariaException_quandoTesourariaFechada() {
        when(tesourariaRepository.findByDataFechamentoIsNull()).thenReturn(Optional.empty());

        ConflictTesourariaException exception = assertThrows(ConflictTesourariaException.class,
                () -> movimentacaoService.efetuarMovimentoEntrada(requestMovimentoDto, authentication));

        assertEquals("Efetue abertura de tesouraria.", exception.getMessage(), "mensagem de erro não coincide.");
        verifyNoInteractions(movimentoRepository);
        verifyNoMoreInteractions(tesourariaRepository);
    }

    @Test
    void efetuarMovimentoSaida_deveRetornarResponseTesourariaMovimentoDto_quandoSucesso() {
        movimentoSalvo.setTipoMovimento(TipoMovimento.SAIDA);

        responseMovimentoDto = new ResponseTesourariaMovimentoDto(
                movimentoSalvo.getId(),
                movimentoSalvo.getDataMovimentacao(),
                TipoMovimento.SAIDA.name(), // ← ALTERADO para SAIDA
                FormaPagamento.DINHEIRO.name(),
                BigDecimal.valueOf(10),
                "teste",
                "usuarioTeste"
        );

        when(tesourariaRepository.findByDataFechamentoIsNull()).thenReturn(Optional.of(tesourariaAberta));
        when(authentication.getName()).thenReturn("usuarioTeste");
        when(movimentoRepository.save(any(TesourariaMovimento.class))).thenReturn(movimentoSalvo);
        when(movimentoMapper.toResponse(any(TesourariaMovimento.class))).thenReturn(responseMovimentoDto);

        ResponseTesourariaMovimentoDto resultado =
                movimentacaoService.efetuarMovimentoSaida(requestMovimentoDto, authentication);


        assertNotNull(resultado, "não deveria ser nulo.");
        assertEquals(movimentoSalvo.getId(), resultado.idMovimento(), "id não coincide");
        assertEquals(movimentoSalvo.getDataMovimentacao(), resultado.dataMovimento(), "data não coincide");
        assertEquals(movimentoSalvo.getTipoMovimento().name(), resultado.tipoMovimento(), "tipo de movimento não coincide");
        assertEquals(movimentoSalvo.getFormaPagamento().name(), resultado.formaPagamento(), "forma de pagamento não coincide");
        assertEquals(movimentoSalvo.getValor(), resultado.valor(), "valor não coincide");
        assertEquals(movimentoSalvo.getValor(), resultado.valor(), "valor não coincide");
        assertEquals(movimentoSalvo.getDescricao(), resultado.descricao(), "descrição não coincide");
        assertEquals(movimentoSalvo.getUsuario(), resultado.usuarioMovimento(), "nome de usuario não coincide");

        verify(tesourariaRepository, times(1)).findByDataFechamentoIsNull();
        verify(movimentoRepository, times(1)).save(any(TesourariaMovimento.class));
        verify(tesourariaRepository, times(1)).save(tesourariaAberta);
        verify(movimentoMapper, times(1)).toResponse(movimentoSalvo);
        verifyNoMoreInteractions(tesourariaRepository);
        verifyNoMoreInteractions(movimentoRepository);
    }

    @Test
    void efetuarMovimentoSaida_deveRetornarConflictTesourariaException_quandoTesourariaFechada() {
        when(tesourariaRepository.findByDataFechamentoIsNull()).thenReturn(Optional.empty());

        ConflictTesourariaException exception = assertThrows(ConflictTesourariaException.class,
                () -> movimentacaoService.efetuarMovimentoEntrada(requestMovimentoDto, authentication));

        assertEquals("Efetue abertura de tesouraria.", exception.getMessage(), "mensagem de erro não coincide.");
        verifyNoInteractions(movimentoRepository);
        verifyNoMoreInteractions(tesourariaRepository);
    }

}