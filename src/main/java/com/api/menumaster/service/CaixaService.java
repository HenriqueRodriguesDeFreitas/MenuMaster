package com.api.menumaster.service;

import com.api.menumaster.dtos.request.RequestAbrirCaixaDto;
import com.api.menumaster.dtos.response.ResponseAbrirCaixaDto;
import com.api.menumaster.dtos.response.ResponseCaixaDto;
import com.api.menumaster.exception.custom.ConflictTesourariaException;
import com.api.menumaster.model.Caixa;
import com.api.menumaster.model.CaixaMovimento;
import com.api.menumaster.model.Tesouraria;
import com.api.menumaster.model.enums.StatusPedido;
import com.api.menumaster.repository.CaixaRepository;
import com.api.menumaster.repository.TesourariaRepository;
import com.api.menumaster.service.util.CaixaCalculator;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CaixaService {

    private final CaixaRepository caixaRepository;
    private final TesourariaRepository tesourariaRepository;
    private final CaixaCalculator caixaCalculator;

    public CaixaService(CaixaRepository caixaRepository, TesourariaRepository tesourariaRepository,
                        CaixaCalculator caixaCalculator) {
        this.caixaRepository = caixaRepository;
        this.tesourariaRepository = tesourariaRepository;
        this.caixaCalculator = caixaCalculator;
    }

    @Transactional
    public ResponseAbrirCaixaDto abrirCaixa(RequestAbrirCaixaDto dto, Authentication authentication) {

        if (dto.saldoInicial() == null) {
            throw new IllegalArgumentException("Saldo inicial não pode ser nulo");
        }

        Tesouraria tesourariaAberta = tesourariaRepository.findByDataFechamentoIsNull()
                .orElseThrow(() -> new ConflictTesourariaException("Não é possível abrir o caixa: " +
                        "nenhuma tesouraria está aberta."));

        verificarUsuarioPossuiCaixaAbertoParaAbertura(authentication.getName());

        Caixa caixa = new Caixa();
        caixa.setUsuarioUtilizando(authentication.getName());
        caixa.setDataAbertura(LocalDateTime.now());
        caixa.setTesouraria(tesourariaAberta);

        if (dto.saldoInicial().compareTo(BigDecimal.ZERO) >= 0) {
            CaixaMovimento movimento = CaixaMovimento.criarMovimentoSaldoInicial(authentication.getName(),
                    dto.saldoInicial(), caixa);
            caixa.setSaldoInicial(dto.saldoInicial());
            caixa.getMovimentacoesCaixa().add(movimento);
        } else {
            throw new ConflictTesourariaException("Saldo inicial não pode ser negativo.");
        }
        tesourariaAberta.getCaixas().add(caixa);
        return toResponseDto(caixaRepository.save(caixa));
    }

    public List<ResponseCaixaDto> buscarCaixas() {
        List<Caixa> caixas = caixaRepository.findAll();
        return toResponseCaixaDto(caixas);

    }

    @Transactional
    public ResponseCaixaDto fecharCaixa(Authentication authentication) {
        Caixa caixa = verificarUsuarioPossuiCaixaAbertoParaFechamento(authentication.getName());

        validarPermissaoFechamento(caixa, authentication);

        caixa.validarPedidoEstaoFinalizadosOuCanceladosParaFechamento();
        caixa.setSaldoFinal(caixaCalculator.calcularSaldoFinal(caixa));
        caixa.setDataFechamento(LocalDateTime.now());


        return toResponseCaixaDto(caixaRepository.save(caixa));
    }

    private void verificarUsuarioPossuiCaixaAbertoParaAbertura(String usuario) {
        Optional<Caixa> caixa = caixaRepository.findByUsuarioUtilizandoAndDataFechamentoIsNull(usuario);
        if (caixa.isPresent()) {
            throw new ConflictTesourariaException("Usuario já possui um caixa aberto.");
        }
    }

    private Caixa verificarUsuarioPossuiCaixaAbertoParaFechamento(String usuario) {
        Optional<Caixa> caixa = caixaRepository.findByUsuarioUtilizandoAndDataFechamentoIsNull(usuario);
        if (caixa.isEmpty()) {
            throw new ConflictTesourariaException("Usuário não possui caixas abertos.");
        } else {
            return caixa.get();
        }
    }

    private void validarPermissaoFechamento(Caixa caixa, Authentication authentication) {
        if (!caixa.getUsuarioUtilizando().equals(authentication.getName())) {
            boolean isAutorizado = authentication.getAuthorities().stream()
                    .anyMatch(g -> g.getAuthority().equals("CAIXA_FECHAR"));

            if (!isAutorizado) {
                throw new AccessDeniedException("Somente o usuario que abriu o caixa" +
                        " ou com permissão podem fechalo");
            }
        }
    }

    private List<ResponseCaixaDto> toResponseCaixaDto(List<Caixa> caixas) {
        return caixas.stream()
                .map(this::toResponseCaixaDto).toList();
    }

    private ResponseCaixaDto toResponseCaixaDto(Caixa caixa) {
        return new ResponseCaixaDto(caixa.getId(), caixa.getSaldoInicial(), caixa.getSaldoFinal(), caixa.getDataAbertura(), caixa.getDataFechamento(), caixa.getUsuarioUtilizando());
    }

    private ResponseAbrirCaixaDto toResponseDto(Caixa caixa) {
        return new ResponseAbrirCaixaDto(caixa.getUsuarioUtilizando(), caixa.getDataAbertura(),
                caixa.getSaldoInicial());
    }
}
