package com.api.menumaster.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tesouraria")
public class Tesouraria {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "data_abertura")
    private LocalDate dataAbertura = LocalDate.now();

    @Column(name = "saldo_inicial", precision = 10, scale = 2)
    private BigDecimal saldoInicial = BigDecimal.ZERO;

    @Column(name = "saldo_final", precision = 10, scale = 2)
    private BigDecimal saldoFinal = BigDecimal.ZERO;

    @Column(name = "data_fechamento")
    private LocalDate dataFechamento;

    @Column(name = "usuario_abertura", length = 20, nullable = false)
    private String usuarioAbertura;

    @Column(name = "usuario_fechamento",length = 20)
    private String usuarioFechamento;

    @OneToMany(mappedBy = "tesouraria")
    private List<TesourariaMovimentacao> movimentacao;

    public Tesouraria(){}

    public Tesouraria( BigDecimal saldoInicial, String usuarioAbertura) {
        this.dataAbertura = LocalDate.now();
        this.saldoInicial = saldoInicial;
        this.usuarioAbertura = usuarioAbertura;
    }

    public UUID getId() {
        return id;
    }

    public LocalDate getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDate dataAbertura) {
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

    public LocalDate getDataFechamento() {
        return dataFechamento;
    }

    public void setDataFechamento(LocalDate dataFechamento) {
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
}
