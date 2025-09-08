package com.api.menumaster.service;

import com.api.menumaster.dtos.request.RequestMovimentoCaixaDto;
import com.api.menumaster.dtos.response.ResponseMovimentoCaixaDto;
import com.api.menumaster.exception.custom.ConflictTesourariaException;
import com.api.menumaster.model.Caixa;
import com.api.menumaster.model.CaixaMovimento;
import com.api.menumaster.model.enums.FormaPagamento;
import com.api.menumaster.repository.CaixaMovimentoRepository;
import com.api.menumaster.repository.CaixaRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class CaixaMovimentoService {

    private final CaixaMovimentoRepository movimentoRepository;
    private final CaixaRepository caixaRepository;

    public CaixaMovimentoService(CaixaMovimentoRepository movimentoRepository,
                                 CaixaRepository caixaRepository) {
        this.movimentoRepository = movimentoRepository;
        this.caixaRepository = caixaRepository;
    }

    @Transactional
    public ResponseMovimentoCaixaDto adicionarDinheiro(RequestMovimentoCaixaDto dto,
                                                       Authentication authentication) {
        validarValorMovimentoMaiorQueZero(dto);

        Caixa caixaAberto = validarCaixaAberto(authentication.getName());

        var movimento = construirMovimento(dto, authentication, caixaAberto);


        return toResponseDto(movimentoRepository.save(movimento));
    }

    @Transactional
    public ResponseMovimentoCaixaDto retirarDinheiro(RequestMovimentoCaixaDto dto,
                                                     Authentication authentication){
        validarValorMovimentoMaiorQueZero(dto);
        Caixa caixaAberto = validarCaixaAberto(authentication.getName());
        var movimento = construirMovimento(dto, authentication, caixaAberto);
        return toResponseDto(movimentoRepository.save(movimento));
    }

    private Caixa validarCaixaAberto(String usuario) {
        return caixaRepository.findByUsuarioUtilizandoAndDataFechamentoIsNull(usuario)
                .orElseThrow(() -> new ConflictTesourariaException("Operação não permitida: caixa fechado ou não iniciado"));

    }

    private ResponseMovimentoCaixaDto toResponseDto(CaixaMovimento movimento) {
        return new ResponseMovimentoCaixaDto(movimento.getUsuarioMovimento(), movimento.getDataMovimento(),
                movimento.getValor(), movimento.getTipoMovimento(), movimento.getFormaPagamento(),
                movimento.getDescricao());
    }

    private CaixaMovimento construirMovimento(RequestMovimentoCaixaDto dto,
                                              Authentication authentication,
                                              Caixa caixaAberto){
        CaixaMovimento movimento =  new CaixaMovimento();
        movimento.setUsuarioMovimento(authentication.getName());
        movimento.setDataMovimento(LocalDateTime.now());
        movimento.setTipoMovimento(dto.tipoMovimento());
        movimento.setValor(dto.valor());
        movimento.setDescricao(dto.descricao());
        movimento.setFormaPagamento(FormaPagamento.DINHEIRO);
        movimento.setCaixa(caixaAberto);

        return movimento;
    }

    private void validarValorMovimentoMaiorQueZero(RequestMovimentoCaixaDto dto){
        if (dto.valor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor do movimento deve ser positivo");
        }
    }
}
