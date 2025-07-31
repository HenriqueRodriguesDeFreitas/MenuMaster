package com.api.menumaster.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tesouraria")
public class Tesouraria {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "data_abertura", nullable = false)
    private LocalDateTime dataAbertura = LocalDateTime.now();

    @Column(name = "data_fechamento")
    private LocalDateTime dataFechamento;

    @Column(name = "data_reabertura")
    private LocalDateTime dataReabertura;

    @Column(name = "saldo_inicial", precision = 10, scale = 2)
    private BigDecimal saldoInicial = BigDecimal.ZERO;

    @Column(name = "saldo_final", precision = 10, scale = 2)
    private BigDecimal saldoFinal = BigDecimal.ZERO;


    @Column(name = "usuario_abertura", length = 20, nullable = false)
    private String usuarioAbertura;

    @Column(name = "usuario_fechamento", length = 20)
    private String usuarioFechamento;

    @Column(name = "usuario_reabertura", length = 20)
    private String usuarioReabertura;

    @OneToMany(mappedBy = "tesouraria", cascade = CascadeType.ALL)
    private List<TesourariaMovimentacao> movimentacao = new ArrayList<>();

    public Tesouraria() {
    }

    public Tesouraria(BigDecimal saldoInicial, String usuarioAbertura) {
        this.dataAbertura = LocalDateTime.now();
        this.saldoInicial = saldoInicial;
        this.usuarioAbertura = usuarioAbertura;
    }

    public UUID getId() {
        return id;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDateTime dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(BigDecimal saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public BigDecimal getSaldoFinal() {
        return saldoFinal;
    }

    public void setSaldoFinal(BigDecimal saldoFinal) {
        this.saldoFinal = saldoFinal;
    }

    public LocalDateTime getDataFechamento() {
        return dataFechamento;
    }

    public void setDataFechamento(LocalDateTime dataFechamento) {
        this.dataFechamento = dataFechamento;
    }

    public String getUsuarioAbertura() {
        return usuarioAbertura;
    }

    public void setUsuarioAbertura(String usuarioAbertura) {
        this.usuarioAbertura = usuarioAbertura;
    }

    public String getUsuarioFechamento() {
        return usuarioFechamento;
    }

    public void setUsuarioFechamento(String usuarioFechamento) {
        this.usuarioFechamento = usuarioFechamento;
    }

    public List<TesourariaMovimentacao> getMovimentacao() {
        return movimentacao;
    }

    public void setMovimentacao(List<TesourariaMovimentacao> movimentacao) {
        this.movimentacao = movimentacao;
    }

    public String getUsuarioReabertura() {
        return usuarioReabertura;
    }

    public void setUsuarioReabertura(String usuarioReabertura) {
        this.usuarioReabertura = usuarioReabertura;
    }

    public LocalDateTime getDataReabertura() {
        return dataReabertura;
    }

    public void setDataReabertura(LocalDateTime dataReabertura) {
        this.dataReabertura = dataReabertura;
    }

    public void calcularSaldoFinal() {
        if (movimentacao == null || movimentacao.isEmpty()) {
            this.saldoFinal = this.saldoInicial;
            return;
        }
        BigDecimal totalEntrada = movimentacao.stream()
                .filter(t -> TipoMovimento.ENTRADA.equals(t.getTipoMovimento()))
                .map(TesourariaMovimentacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSaida = movimentacao.stream()
                .filter(t -> TipoMovimento.SAIDA.equals(t.getTipoMovimento()))
                .map(TesourariaMovimentacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.saldoFinal = this.saldoInicial.add(totalEntrada).subtract(totalSaida);
    }

}
