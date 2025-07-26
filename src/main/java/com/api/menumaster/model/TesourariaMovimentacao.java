package com.api.menumaster.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "tesouraria_movimentacao")
public class TesourariaMovimentacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "tesouraria_id")
    private Tesouraria tesouraria;

    @Column(name = "data_movimento", nullable = false)
    private LocalDate dataMovimentacao;

    @Column(name = "tipo_movimento", nullable = false, length = 20)
    private String tipoMovimento;

    @Column(name = "forma_pagamento", length = 20, nullable = false)
    private String formaPagamento;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal valor;

    @Column(length = 200)
    private String descricao;

    @Column(length = 20, nullable = false)
    private String usuario;

    public TesourariaMovimentacao(Tesouraria tesouraria, LocalDate dataMovimentacao, String tipoMovimento, String formaPagamento, BigDecimal valor, String usuario, String descricao) {
        this.tesouraria = tesouraria;
        this.dataMovimentacao = dataMovimentacao;
        this.tipoMovimento = tipoMovimento;
        this.formaPagamento = formaPagamento;
        this.valor = valor;
        this.usuario = usuario;
        this.descricao = descricao;
    }

    public UUID getId() {
        return id;
    }

    public Tesouraria getTesouraria() {
        return tesouraria;
    }

    public void setTesouraria(Tesouraria tesouraria) {
        this.tesouraria = tesouraria;
    }

    public LocalDate getDataMovimentacao() {
        return dataMovimentacao;
    }

    public void setDataMovimentacao(LocalDate dataMovimentacao) {
        this.dataMovimentacao = dataMovimentacao;
    }

    public String getTipoMovimento() {
        return tipoMovimento;
    }

    public void setTipoMovimento(String tipoMovimento) {
        this.tipoMovimento = tipoMovimento;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }


}
