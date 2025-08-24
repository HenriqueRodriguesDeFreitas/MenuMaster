package com.api.menumaster.service;

import com.api.menumaster.dtos.response.ResponseTesourariaDto;
import com.api.menumaster.exception.custom.ConflictTesourariaException;
import com.api.menumaster.model.Tesouraria;
import com.api.menumaster.repository.TesourariaRepository;
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
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TesourariaServiceTest {

    @Mock
    private TesourariaRepository tesourariaRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TesourariaService tesourariaService;

    private Tesouraria tesourariaSalva;
    private Tesouraria tesourariaFechada;

    private ResponseTesourariaDto responseTesourariaDto;
    private LocalDateTime hoje;
    private final String mensagemValidacaoDadosRetornadorTesouraria = "Validando dados de retornados de tesouraria.";

    @BeforeEach
    void setUp() {
        tesourariaSalva = new Tesouraria(BigDecimal.ZERO, "usuarioTest");
        tesourariaSalva.setId(UUID.randomUUID());
        tesourariaSalva.setDataAbertura(LocalDateTime.now());

        hoje = LocalDateTime.now();

        tesourariaFechada = new Tesouraria(BigDecimal.ZERO, "usuarioTest");
        tesourariaFechada.setId(UUID.randomUUID());
        tesourariaFechada.setDataAbertura(hoje);
        tesourariaFechada.setDataFechamento(hoje);
        tesourariaFechada.setUsuarioFechamento("usuarioFechamentoTest");
        tesourariaFechada.setUsuarioReabertura(authentication.getName());
    }

    @Test
    void abrirTesouraria_deveRetornarResponseTesourariaDto_quandoSucesso() {
        UUID idTesourariaSalva = tesourariaSalva.getId();
        when(tesourariaRepository.existsByDataFechamentoIsNull()).thenReturn(false);
        when(tesourariaRepository.findFirstByDataFechamentoIsNotNullOrderByDataFechamentoDesc())
                .thenReturn(Optional.empty());
        when(tesourariaRepository.findByDataFechamentoBetween(
                hoje.toLocalDate().atStartOfDay(),
                hoje.toLocalDate().atTime(23, 59, 59)
        )).thenReturn(List.of());

        when(authentication.getName()).thenReturn("usuarioTest");

        when(tesourariaRepository.save(any(Tesouraria.class))).thenReturn(tesourariaSalva);

        responseTesourariaDto = tesourariaService.abrirTesouraria(authentication);

        assertAll(mensagemValidacaoDadosRetornadorTesouraria,
                () -> validarTodosDadosTesouraria(tesourariaSalva, responseTesourariaDto));

        verify(tesourariaRepository, times(1)).existsByDataFechamentoIsNull();
        verify(tesourariaRepository, times(1)).findByDataFechamentoBetween(hoje.toLocalDate().atStartOfDay(),
                hoje.toLocalDate().atTime(23, 59, 59));
        verify(tesourariaRepository, times(1)).save(any(Tesouraria.class));
    }

    @Test
    void abrirTesouraria_deveConflictTesourariaException_quandoJaExisteTesourariaAberta() {
        when(tesourariaRepository.existsByDataFechamentoIsNull()).thenReturn(true);

        ConflictTesourariaException exception = assertThrows(ConflictTesourariaException.class,
                () -> tesourariaService.abrirTesouraria(authentication));

        assertEquals("Existe tesouraria aberta", exception.getMessage(), "Mensagem não coincide.");

        verifyNoMoreInteractions(tesourariaRepository);
    }

    @Test
    void abrirTesouraria_deveConflictTesourariaException_quandoJaTeveTesourariaAbertaEFechadaHoje() {
        when(tesourariaRepository.existsByDataFechamentoIsNull()).thenReturn(false);

        Tesouraria tesourariaFechadaHoje = new Tesouraria(BigDecimal.ZERO, "usuarioTest");
        tesourariaFechadaHoje.setDataAbertura(hoje);
        tesourariaFechadaHoje.setDataFechamento(hoje);

        when(tesourariaRepository.findByDataFechamentoBetween(hoje.toLocalDate().atStartOfDay(),
                hoje.toLocalDate().atTime(23, 59, 59)))
                .thenReturn(List.of(tesourariaFechadaHoje));

        ConflictTesourariaException conflict = assertThrows(ConflictTesourariaException.class,
                () -> tesourariaService.abrirTesouraria(authentication));

        assertEquals("Existe tesouraria aberta e fechada hoje. Utilize o endpoint para reabertura.",
                conflict.getMessage());
        verifyNoMoreInteractions(tesourariaRepository);
    }

    @Test
    void fecharTesouraria_deveRetornarResponseTesourariaDto_quandoSucesso() {
        when(tesourariaRepository.findByDataFechamentoIsNull()).thenReturn(Optional.of(tesourariaSalva));
        when(tesourariaRepository.save(any(Tesouraria.class))).thenReturn(tesourariaSalva);

        responseTesourariaDto = tesourariaService.fecharTesouraria(authentication);

        assertAll(mensagemValidacaoDadosRetornadorTesouraria,
                () -> validarTodosDadosTesouraria(tesourariaSalva, responseTesourariaDto));

        verify(tesourariaRepository, times(1)).findByDataFechamentoIsNull();
        verify(tesourariaRepository, times(1)).save(tesourariaSalva);
        verifyNoMoreInteractions(tesourariaRepository);
    }

    @Test
    void fecharTesouraria_deveConflictTesourariaException_quandoNaoExisteTesourariaAberta() {
        when(tesourariaRepository.findByDataFechamentoIsNull()).thenReturn(Optional.empty());

        ConflictTesourariaException exception = assertThrows(ConflictTesourariaException.class,
                () -> tesourariaService.fecharTesouraria(authentication));

        assertEquals("Não existe tesouraria aberta para fechar.", exception.getMessage(),
                "mensgens de erro não coincidem.");

        verifyNoMoreInteractions(tesourariaRepository);
    }

    @Test
    void reabrirTesouraria_deveRetornarResponseTesourariaDto_quandoSucesso() {
        tesourariaFechada.setDataAbertura(hoje.minusDays(1));
        tesourariaFechada.setDataFechamento(hoje.minusHours(1));

        when(tesourariaRepository.findFirstByDataFechamentoIsNotNullOrderByDataFechamentoDesc())
                .thenReturn(Optional.of(tesourariaFechada));
        when(tesourariaRepository.save(any(Tesouraria.class))).thenReturn(tesourariaFechada);
        when(authentication.getName()).thenReturn("usuarioFechamentoTest");

        responseTesourariaDto = tesourariaService.reabrirTesouraria(authentication);

        assertAll(mensagemValidacaoDadosRetornadorTesouraria,
                () -> validarTodosDadosTesouraria(tesourariaFechada, responseTesourariaDto));

        verify(tesourariaRepository, times(1)).findFirstByDataFechamentoIsNotNullOrderByDataFechamentoDesc();
        verify(tesourariaRepository, times(1)).save(tesourariaFechada);
        verifyNoMoreInteractions(tesourariaRepository);
    }

    @Test
    void reabrirTesouraria_deveRetornarConflictTesourariaException_quandoNaoTemTesourariaParaReabrir() {
        when(tesourariaRepository.findFirstByDataFechamentoIsNotNullOrderByDataFechamentoDesc())
                .thenReturn(Optional.empty());

        ConflictTesourariaException exception = assertThrows(ConflictTesourariaException.class,
                () -> tesourariaService.reabrirTesouraria(authentication));

        assertEquals("Não existe tesouraria para reabrir.", exception.getMessage());
        verify(tesourariaRepository, times(1)).findFirstByDataFechamentoIsNotNullOrderByDataFechamentoDesc();
        verifyNoMoreInteractions(tesourariaRepository);
    }

    @Test
    void reabrirTesouraria_deveRetornarConflictTesourariaException_quandoDataReaberturaAnteriorDataFechamento() {
        tesourariaFechada.setDataFechamento(hoje.plusHours(1));

        when(tesourariaRepository.findFirstByDataFechamentoIsNotNullOrderByDataFechamentoDesc())
                .thenReturn(Optional.of(tesourariaFechada));

        ConflictTesourariaException exception = assertThrows(ConflictTesourariaException.class,
                () -> tesourariaService.reabrirTesouraria(authentication));

        assertEquals("Erro inesperado, a data de reabertura precisa ser depois do fechamento, contate o suporte.",
                exception.getMessage());
        verifyNoMoreInteractions(tesourariaRepository);
    }

    @Test
    void buscarTesourariasPorDataAbertura_dataFinalNaoNula_deveRetornarResponseTesourariaDto_quandoSucesso() {
        LocalDate dataInicial = LocalDate.of(2025, 1, 1);
        LocalDate dataFinal = LocalDate.of(2025, 1, 5);
        LocalTime tempoPadrao = LocalTime.of(0, 0, 0);

        LocalDateTime dataHoraTesourariaInicio = LocalDateTime.of(dataInicial.getYear(), dataInicial.getMonth(),
                dataInicial.getDayOfMonth(), tempoPadrao.getHour(), tempoPadrao.getMinute(), tempoPadrao.getSecond());

        List<Tesouraria> tesourariasSalvas = listaTesourarias();

        LocalDateTime dataHoraTesourariaFim = LocalDateTime.of(dataFinal.getYear(), dataFinal.getMonth(),
                dataFinal.getDayOfMonth(), 23, 59, 59);

        when(tesourariaRepository.findByDataAberturaBetweenOrderByDataAbertura(
                dataHoraTesourariaInicio, dataHoraTesourariaFim)).thenReturn(tesourariasSalvas);

        List<ResponseTesourariaDto> responses =
                tesourariaService.buscarTesourariasPorDataAbertura(dataInicial, dataFinal);

        assertAll("Validando dados retornados na lista",
                () -> validarTodosDadosTesouraria(tesourariasSalvas, responses));

        verify(tesourariaRepository, times(1))
                .findByDataAberturaBetweenOrderByDataAbertura(dataHoraTesourariaInicio, dataHoraTesourariaFim);
        verifyNoMoreInteractions(tesourariaRepository);
    }

    @Test
    void buscarTesourariasPorDataAbertura_dataFinalNula_deveRetornarResponseTesourariaDto_quandoSucesso() {
        LocalDate dataInicial = LocalDate.of(2025, 1, 5);

        LocalDateTime dataHoraTesourariaBuscar = LocalDateTime.of(dataInicial.getYear(), dataInicial.getMonth(),
                dataInicial.getDayOfMonth(), 23, 59, 59);

        List<Tesouraria> tesourariasSalvas = listaTesourarias();

        when(tesourariaRepository
                .findByDataAberturaBetweenOrderByDataAbertura(dataHoraTesourariaBuscar, dataHoraTesourariaBuscar))
                .thenReturn(tesourariasSalvas);

        List<ResponseTesourariaDto> responses =
                tesourariaService.buscarTesourariasPorDataAbertura(dataInicial, null);

        assertAll("Validando dados retornados na lista",
                () -> validarTodosDadosTesouraria(tesourariasSalvas, responses));

        verify(tesourariaRepository, times(1))
                .findByDataAberturaBetweenOrderByDataAbertura(
                        dataHoraTesourariaBuscar, dataHoraTesourariaBuscar);
        verifyNoMoreInteractions(tesourariaRepository);


    }

    @Test
    void buscarTesourariasPorDataAbertura_dataFinalNaoNula_deveRetornarConflictTesourariaException_quandoDataFinalAnteriorInicial() {
        LocalDate dataInicial = LocalDate.of(2025, 1, 10);
        LocalDate dataFinal = LocalDate.of(2025, 1, 5);

        ConflictTesourariaException exception = assertThrows(ConflictTesourariaException.class,
                () -> tesourariaService.buscarTesourariasPorDataAbertura(dataInicial, dataFinal));

        assertEquals("Data final não pode ser anterior a inicial.", exception.getMessage());
        verifyNoMoreInteractions(tesourariaRepository);
    }

    private List<Tesouraria> listaTesourarias() {
        tesourariaFechada.setDataAbertura(
                LocalDateTime.of(2025, 2, 1, 1, 1, 1));
        tesourariaSalva.setDataAbertura(
                LocalDateTime.of(2025, 3, 1, 1, 1, 1));
        return List.of(tesourariaSalva, tesourariaFechada);
    }

    private void validarTodosDadosTesouraria(Tesouraria tesouraria, ResponseTesourariaDto responseTesourariaDto) {
        assertNotNull(responseTesourariaDto, "retorno não deveria ser nulo.");
        assertEquals(tesouraria.getId(), responseTesourariaDto.id());
        assertEquals(tesouraria.getDataAbertura(), responseTesourariaDto.dataAbertura(), "Datas abertura não coincidem.");
        assertEquals(tesouraria.getDataFechamento(), responseTesourariaDto.dataFechamento(), "Datas fechamento não coincidem.");
        assertEquals(tesouraria.getDataReabertura(), responseTesourariaDto.dataReabertura(), "Datas reabertura não coincidem.");
        assertEquals(tesouraria.getSaldoInicial(), responseTesourariaDto.saldoInicial(), "Saldo inicial não coincidem.");
        assertEquals(tesouraria.getSaldoFinal(), responseTesourariaDto.saldoFinal(), "Saldo final não coincidem");
        assertEquals(tesouraria.getUsuarioAbertura(), responseTesourariaDto.usuarioAbertura(), "Usuarios abertura não coincidem");
        assertEquals(tesouraria.getUsuarioReabertura(), responseTesourariaDto.usuarioReabertura(), "Usuarios reabertura não coincidem");
        assertEquals(tesouraria.getUsuarioFechamento(), responseTesourariaDto.usuarioFechamento(), "Usuarios fechamento não coincidem");
    }

    private void validarTodosDadosTesouraria(List<Tesouraria> tesourariasSalvas,
                                             List<ResponseTesourariaDto> responses) {
        assertNotNull(responses);
        assertEquals(tesourariasSalvas.get(0).getId(), responses.get(0).id(), "id não coincidem.");
        assertEquals(tesourariasSalvas.get(0).getDataAbertura(), responses.get(0).dataAbertura(), "Datas abertura não coincidem.");
        assertEquals(tesourariasSalvas.get(0).getDataFechamento(), responses.get(0).dataFechamento(), "Datas fechamento não coincidem.");
        assertEquals(tesourariasSalvas.get(0).getDataReabertura(), responses.get(0).dataReabertura(), "Datas reabertura não coincidem.");
        assertEquals(tesourariasSalvas.get(0).getSaldoInicial(), responses.get(0).saldoInicial(), "Saldo inicial não coincidem.");
        assertEquals(tesourariasSalvas.get(0).getSaldoFinal(), responses.get(0).saldoFinal(), "Saldo final não coincidem");
        assertEquals(tesourariasSalvas.get(0).getUsuarioAbertura(), responses.get(0).usuarioAbertura(), "Usuarios abertura não coincidem");
        assertEquals(tesourariasSalvas.get(0).getUsuarioReabertura(), responses.get(0).usuarioReabertura(), "Usuarios reabertura não coincidem");
        assertEquals(tesourariasSalvas.get(0).getUsuarioFechamento(), responses.get(0).usuarioFechamento(), "Usuarios fechamento não coincidem");
        assertEquals(tesourariasSalvas.get(1).getId(), responses.get(1).id(), "id não coincidem.");
        assertEquals(tesourariasSalvas.get(1).getDataAbertura(), responses.get(1).dataAbertura(), "Datas abertura não coincidem.");
        assertEquals(tesourariasSalvas.get(1).getDataFechamento(), responses.get(1).dataFechamento(), "Datas fechamento não coincidem.");
        assertEquals(tesourariasSalvas.get(1).getDataReabertura(), responses.get(1).dataReabertura(), "Datas reabertura não coincidem.");
        assertEquals(tesourariasSalvas.get(1).getSaldoInicial(), responses.get(1).saldoInicial(), "Saldo inicial não coincidem.");
        assertEquals(tesourariasSalvas.get(1).getSaldoFinal(), responses.get(1).saldoFinal(), "Saldo final não coincidem");
        assertEquals(tesourariasSalvas.get(1).getUsuarioAbertura(), responses.get(1).usuarioAbertura(), "Usuarios abertura não coincidem");
        assertEquals(tesourariasSalvas.get(1).getUsuarioReabertura(), responses.get(1).usuarioReabertura(), "Usuarios reabertura não coincidem");
        assertEquals(tesourariasSalvas.get(1).getUsuarioFechamento(), responses.get(1).usuarioFechamento(), "Usuarios fechamento não coincidem");
    }
}