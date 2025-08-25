package com.api.menumaster.service;

import com.api.menumaster.dtos.request.RequestMovimentoTesourariaDto;
import com.api.menumaster.dtos.response.ResponseTesourariaMovimentoDto;
import com.api.menumaster.exception.custom.ConflictTesourariaException;
import com.api.menumaster.mappper.TesourariaMovimentoMapper;
import com.api.menumaster.model.TesourariaMovimento;
import com.api.menumaster.model.enums.TipoMovimento;
import com.api.menumaster.repository.TesourariaMovimentacaoRepository;
import com.api.menumaster.repository.TesourariaRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TesourariaMovimentoService {

    private final TesourariaRepository tesourariaRepository;
    private final TesourariaMovimentacaoRepository movimentacaoRepository;
    private final TesourariaMovimentoMapper movimentoMapper;

    public TesourariaMovimentoService(TesourariaRepository tesourariaRepository,
                                      TesourariaMovimentacaoRepository movimentacaoRepository,
                                      TesourariaMovimentoMapper movimentoMapper) {
        this.tesourariaRepository = tesourariaRepository;
        this.movimentacaoRepository = movimentacaoRepository;
        this.movimentoMapper = movimentoMapper;
    }

    @Transactional
    public ResponseTesourariaMovimentoDto efetuarMovimentoEntrada(RequestMovimentoTesourariaDto dto,
                                                                  Authentication authentication) {
        var tesourariaAberta = tesourariaRepository.findByDataFechamentoIsNull()
                .orElseThrow(() -> new ConflictTesourariaException("Efetue abertura de tesouraria."));

        TesourariaMovimento movimentacao = new TesourariaMovimento();
        movimentacao.setDataMovimentacao(LocalDateTime.now());
        movimentacao.setTipoMovimento(TipoMovimento.ENTRADA);
        movimentacao.setFormaPagamento(dto.formaPagamento());
        movimentacao.setDescricao(dto.descricao());
        movimentacao.setValor(dto.valor());
        movimentacao.setUsuario(authentication.getName());
        movimentacao.setTesouraria(tesourariaAberta);

        var movimentoSalvo = movimentacaoRepository.save(movimentacao);

        tesourariaAberta.getMovimentacao().add(movimentoSalvo);

        tesourariaRepository.save(tesourariaAberta);
        return movimentoMapper.toResponse(movimentoSalvo);
    }

    public ResponseTesourariaMovimentoDto efetuarMovimentoSaida(RequestMovimentoTesourariaDto dto,
                                                                Authentication authentication){
        var tesourariaAberta = tesourariaRepository.findByDataFechamentoIsNull()
                .orElseThrow(() -> new ConflictTesourariaException("Efetue abertura de tesouraria."));

        TesourariaMovimento movimentacao = new TesourariaMovimento();
        movimentacao.setDataMovimentacao(LocalDateTime.now());
        movimentacao.setTipoMovimento(TipoMovimento.SAIDA);
        movimentacao.setFormaPagamento(dto.formaPagamento());
        movimentacao.setDescricao(dto.descricao());
        movimentacao.setValor(dto.valor());
        movimentacao.setUsuario(authentication.getName());
        movimentacao.setTesouraria(tesourariaAberta);

        var movimentoSalvo = movimentacaoRepository.save(movimentacao);

        tesourariaAberta.getMovimentacao().add(movimentoSalvo);

        tesourariaRepository.save(tesourariaAberta);
        return movimentoMapper.toResponse(movimentoSalvo);
    }
}
