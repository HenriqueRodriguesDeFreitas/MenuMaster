package com.api.menumaster.model;

import com.api.menumaster.model.enums.FormaPagamento;
import com.api.menumaster.model.enums.TipoMovimento;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tesouraria_movimentacao")
public class TesourariaMovimento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "tesouraria_id", nullable = false)
    private Tesouraria tesouraria;

    @Column(name = "data_movimento", nullable = false)
    private LocalDateTime dataMovimentacao;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "tipomovimento", name = "tipo_movimento", nullable = false)
    private TipoMovimento tipoMovimento;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "formapagamento", name = "forma_pagamento", nullable = false)
    private FormaPagamento formaPagamento;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal valor;

    @Column(length = 200)
    private String descricao;

    @Column(length = 20, nullable = false)
    private String usuario;

    public TesourariaMovimento() {
    }

    public TesourariaMovimento(Tesouraria tesouraria, LocalDateTime dataMovimentacao,
                               TipoMovimento tipoMovimento, FormaPagamento formaPagamento,
                               BigDecimal valor, String usuario, String descricao) {
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

    public void setId(UUID id) {
        this.id = id;
    }

    public Tesouraria getTesouraria() {
        return tesouraria;
    }

    public void setTesouraria(Tesouraria tesouraria) {
        this.tesouraria = tesouraria;
    }

    public LocalDateTime getDataMovimentacao() {
        return dataMovimentacao;
    }

    public void setDataMovimentacao(LocalDateTime dataMovimentacao) {
        this.dataMovimentacao = dataMovimentacao;
    }

    public TipoMovimento getTipoMovimento() {
        return tipoMovimento;
    }

    public void setTipoMovimento(TipoMovimento tipoMovimento) {
        this.tipoMovimento = tipoMovimento;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
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
