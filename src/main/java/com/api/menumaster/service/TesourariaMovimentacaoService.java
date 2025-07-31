package com.api.menumaster.service;

import com.api.menumaster.dtos.request.RequestMovimentoTesourariaDto;
import com.api.menumaster.dtos.response.ResponseMovimentoTesourariaDto;
import com.api.menumaster.exception.custom.ConflictTesourariaException;
import com.api.menumaster.model.TesourariaMovimentacao;
import com.api.menumaster.repository.TesourariaMovimentacaoRepository;
import com.api.menumaster.repository.TesourariaRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TesourariaMovimentacaoService {

    private final TesourariaRepository tesourariaRepository;
    private final TesourariaMovimentacaoRepository movimentacaoRepository;

    public TesourariaMovimentacaoService(TesourariaRepository tesourariaRepository, TesourariaMovimentacaoRepository movimentacaoRepository) {
        this.tesourariaRepository = tesourariaRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    @Transactional
    public ResponseMovimentoTesourariaDto efetuarMovimento(RequestMovimentoTesourariaDto dto,
                                                           Authentication authentication) {
        var tesourariaAberta = tesourariaRepository.findByDataFechamentoIsNull()
                .orElseThrow(()-> new ConflictTesourariaException("Efetue abertura de tesouraria."));

        TesourariaMovimentacao movimentacao = new TesourariaMovimentacao();
        movimentacao.setDataMovimentacao(LocalDateTime.now());
        movimentacao.setTipoMovimento(dto.tipoMovimento().name());
        movimentacao.setFormaPagamento(dto.formaPagamento().name());
        movimentacao.setDescricao(dto.descricao());
        movimentacao.setValor(dto.valor());
        movimentacao.setUsuario(authentication.getName());
        movimentacao.setTesouraria(tesourariaAberta);

        var movimentoSalvo = movimentacaoRepository.save(movimentacao);

        tesourariaAberta.getMovimentacao().add(movimentoSalvo);
        tesourariaAberta.calcularSaldoFinal();

         tesourariaRepository.save(tesourariaAberta);
       return toResponseDto(movimentoSalvo);
    }


    private ResponseMovimentoTesourariaDto toResponseDto(TesourariaMovimentacao movimentacao){
        return new ResponseMovimentoTesourariaDto(movimentacao.getDataMovimentacao(),  movimentacao.getTipoMovimento(),
                movimentacao.getFormaPagamento(), movimentacao.getValor(), movimentacao.getDescricao(), movimentacao.getUsuario());
    }
}
