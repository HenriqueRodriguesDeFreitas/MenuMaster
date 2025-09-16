package com.api.menumaster.model;

import com.api.menumaster.exception.custom.DadoPassadoNuloException;
import com.api.menumaster.model.enums.FormaPagamento;
import com.api.menumaster.model.enums.TipoMovimento;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "caixa_movimento")
public class CaixaMovimento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "usuario_movimento", length = 20, nullable = false)
    private String usuarioMovimento;

    @Column(name = "data_movimento", nullable = false)
    private LocalDateTime dataMovimento = LocalDateTime.now();

    @Column(name = "valor", precision = 10, scale = 2, nullable = false)
    private BigDecimal valor = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "tipomovimento",name = "tipo_movimento", nullable = false)
    private TipoMovimento tipoMovimento;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "formapagamento",name = "forma_pagamento", nullable = false)
    private FormaPagamento formaPagamento;

    @Column(length = 200)
    private String descricao;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "caixa_id")
    private Caixa caixa;

    public CaixaMovimento() {
    }

    public CaixaMovimento(String usuarioMovimento, LocalDateTime dataMovimento, BigDecimal valor,
                          TipoMovimento tipoMovimento, FormaPagamento formaPagamento, String descricao,
                          Caixa caixa) {
        this.usuarioMovimento = usuarioMovimento;
        this.dataMovimento = LocalDateTime.now();
        this.valor = valor;
        this.tipoMovimento = tipoMovimento;
        this.formaPagamento = formaPagamento;
        this.descricao = descricao;
        this.caixa = caixa;
    }

    public UUID getId() {
        return id;
    }

    public String getUsuarioMovimento() {
        return usuarioMovimento;
    }

    public void setUsuarioMovimento(String usuarioMovimento) {
        this.usuarioMovimento = usuarioMovimento;
    }

    public LocalDateTime getDataMovimento() {
        return dataMovimento;
    }

    public void setDataMovimento(LocalDateTime dataMovimento) {
        this.dataMovimento = dataMovimento;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        if (valor == null) {
            throw new DadoPassadoNuloException("O valor para movimento de caixa não  pode ser nulo.");
        }
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor para movimento de caixa deve ser maior que zero");
        }
        this.valor = valor;
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Caixa getCaixa() {
        return caixa;
    }

    public void setCaixa(Caixa caixa) {
        if (caixa == null) {
            throw new DadoPassadoNuloException("Nenhum caixa associado, associe um caixa para efetuar movimento.");
        }
        this.caixa = caixa;
    }

    public static CaixaMovimento criarMovimentoSaldoInicial(String usuario, BigDecimal valor, Caixa caixa) {
        CaixaMovimento mov = new CaixaMovimento();
        mov.setUsuarioMovimento(usuario);
        mov.setTipoMovimento(TipoMovimento.ENTRADA);
        mov.setFormaPagamento(FormaPagamento.DINHEIRO);
        mov.setDescricao("Entrada de saldo inicial em dinheiro para abertura de caixa");
        mov.setValor(valor);
        mov.setCaixa(caixa);
        return mov;
    }


}
