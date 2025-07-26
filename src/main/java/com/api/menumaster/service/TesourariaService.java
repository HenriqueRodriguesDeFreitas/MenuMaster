package com.api.menumaster.service;

import com.api.menumaster.dtos.response.ResponseTesourariaDto;
import com.api.menumaster.exception.custom.ConflictTesourariaException;
import com.api.menumaster.model.Tesouraria;
import com.api.menumaster.repository.TesourariaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TesourariaService {

    private final TesourariaRepository tesourariaRepository;

    public TesourariaService(TesourariaRepository tesourariaRepository) {
        this.tesourariaRepository = tesourariaRepository;
    }

    public ResponseTesourariaDto abrirTesouraria(Authentication authentication) {
        validarTesourariaAberta();
        validarTesourariaFechadaParaReabrir();

        BigDecimal saldoInicial = calcularSaldoInicial();
        Tesouraria tesouraria = new Tesouraria(saldoInicial, authentication.getName());
        return toResponseDto(tesourariaRepository.save(tesouraria));
    }

    public ResponseTesourariaDto fecharTesouraria(Authentication authentication) {
        Tesouraria tesourariaAberta = tesourariaRepository.findByDataFechamentoIsNull()
                .orElseThrow(() -> new ConflictTesourariaException("Não existe tesouraria aberta para fechar"));

        tesourariaAberta.setDataFechamento(LocalDateTime.now());
        tesourariaAberta.setUsuarioFechamento(authentication.getName());
        tesourariaAberta.calcularSaldoFinal();

        return toResponseDto(tesourariaRepository.save(tesourariaAberta));
    }

    public ResponseTesourariaDto reabrirTesouraria(Authentication authentication) {
        Tesouraria tesouraria = tesourariaRepository.findFirstByDataFechamentoIsNotNullOrderByDataFechamentoDesc()
                .orElseThrow(() -> new ConflictTesourariaException("Não existe tesouraria para reabrir"));

        tesouraria.setDataFechamento(null);
        tesouraria.setUsuarioReabertura(authentication.getName());
        tesouraria.setDataAbertura(tesouraria.getDataAbertura());
        tesouraria.setDataReabertura(LocalDateTime.now());
        return toResponseDto(tesourariaRepository.save(tesouraria));
    }

    private void validarTesourariaAberta() {
        if (tesourariaRepository.existsByDataFechamentoIsNull()) {
            throw new ConflictTesourariaException("Existe tesouraria aberta");
        }
    }

    private void validarTesourariaFechadaParaReabrir() {
        LocalDateTime hoje = LocalDateTime.now();

               tesourariaRepository.findByDataFechamentoBetween(hoje.toLocalDate().atStartOfDay(),
                               hoje.toLocalDate().atTime(23,59,59))
                .stream()
                .filter( tesouraria
                        -> tesouraria.getDataAbertura().toLocalDate().equals(hoje.toLocalDate()))
                .findFirst().ifPresent(t -> { throw new ConflictTesourariaException(
                        "Existe caixa aberto e fechado hoje. Utilize o endpoint para reabertura");});


        // Busca específica por caixas fechados HOJE
        tesourariaRepository.findByDataFechamento(hoje)
                .stream()
                .filter(caixa -> caixa.getDataAbertura().equals(hoje)) // Filtra os que foram abertos hoje
                .findFirst()
                .ifPresent(caixa -> {
                    throw new ConflictTesourariaException(
                            "Existe caixa aberto e fechado hoje. Utilize o endpoint de reabertura");
                });
    }

    private BigDecimal calcularSaldoInicial() {
        return tesourariaRepository
                .findFirstByDataFechamentoIsNotNullOrderByDataFechamentoDesc()
                .filter(this::temSaldoPositivo)
                .map(Tesouraria::getSaldoFinal)
                .orElse(BigDecimal.ZERO);
    }

    private boolean temSaldoPositivo(Tesouraria t) {
        return t.getSaldoFinal() != null && t.getSaldoFinal().compareTo(BigDecimal.ZERO) >= 0;
    }

    private ResponseTesourariaDto toResponseDto(Tesouraria tesouraria) {
        return new ResponseTesourariaDto(tesouraria.getId(), tesouraria.getDataAbertura(),
                tesouraria.getDataFechamento(), tesouraria.getDataReabertura(),
                tesouraria.getSaldoInicial(), tesouraria.getSaldoFinal(), tesouraria.getUsuarioAbertura(),
                tesouraria.getUsuarioFechamento(), tesouraria.getUsuarioReabertura());
    }
}
