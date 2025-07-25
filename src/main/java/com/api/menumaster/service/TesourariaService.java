package com.api.menumaster.service;

import com.api.menumaster.dtos.response.ResponseTesouraria;
import com.api.menumaster.exception.custom.ConflictTesourariaException;
import com.api.menumaster.model.Tesouraria;
import com.api.menumaster.repository.TesourariaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class TesourariaService {

    private final TesourariaRepository tesourariaRepository;

    public TesourariaService(TesourariaRepository tesourariaRepository) {
        this.tesourariaRepository = tesourariaRepository;
    }

    public ResponseTesouraria abrirTesouraria(boolean abrir, Authentication authentication) {

        if (abrir) {
            if (tesourariaRepository.existsByDataFechamentoIsNull()) {
                throw new ConflictTesourariaException("Feche a tesouraria anterior.");
            }

            BigDecimal saldoInicial = BigDecimal.ZERO;
            Optional<Tesouraria> ultimaTesourariaOpt = tesourariaRepository
                    .findFirstByDataFechamentoIsNotNullOrderByDataFechamentoDesc();

            if (ultimaTesourariaOpt.isPresent()) {
                Tesouraria ultimaTesouraria = ultimaTesourariaOpt.get();
                if (ultimaTesouraria.getSaldoFinal() != null
                        && ultimaTesouraria.getSaldoFinal().compareTo(BigDecimal.ZERO) >= 0) {
                    saldoInicial = ultimaTesouraria.getSaldoFinal();
                }
            }
            Tesouraria tesouraria = new Tesouraria(saldoInicial, authentication.getName());
            return converterObjetoParaDto(tesourariaRepository.save(tesouraria));
        } else {
            return null;
        }
    }

    private ResponseTesouraria converterObjetoParaDto(Tesouraria tesouraria) {
        return new ResponseTesouraria(tesouraria.getId(), tesouraria.getDataAbertura(),
                tesouraria.getSaldoInicial(), tesouraria.getSaldoFinal(),
                tesouraria.getDataFechamento(), tesouraria.getUsuarioAbertura(),
                tesouraria.getUsuarioFechamento());
    }
}
