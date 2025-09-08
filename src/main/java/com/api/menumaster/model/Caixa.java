package com.api.menumaster.model;

import com.api.menumaster.exception.custom.ConflictTesourariaException;
import com.api.menumaster.model.enums.StatusPedido;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "caixa")
public class Caixa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "saldo_inicial", precision = 10, scale = 2, nullable = false)
    private BigDecimal saldoInicial = BigDecimal.ZERO;

    @Column(name = "data_abertura", nullable = false)
    private LocalDateTime dataAbertura = LocalDateTime.now();

    @Column(name = "data_fechamento")
    private LocalDateTime dataFechamento;

    @Column(name = "saldo_final", precision = 10, scale = 2)
    private BigDecimal saldoFinal = BigDecimal.ZERO;

    @Column(name = "usuario_utilizando", length = 20, nullable = false)
    private String usuarioUtilizando;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "tesouraria_id", nullable = false)
    private Tesouraria tesouraria;

    @OneToMany(mappedBy = "caixa", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Pedido> pedidos = new ArrayList<>();

    @OneToMany(mappedBy = "caixa", cascade = CascadeType.ALL)
    private List<CaixaMovimento> movimentacoesCaixa = new ArrayList<>();

    public Caixa() {
    }

    public Caixa(BigDecimal saldoInicial, Tesouraria tesouraria) {
        this.dataAbertura = LocalDateTime.now();
        this.saldoInicial = saldoInicial;
        this.tesouraria = tesouraria;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(BigDecimal saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDateTime dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public LocalDateTime getDataFechamento() {
        return dataFechamento;
    }

    public void setDataFechamento(LocalDateTime dataFechamento) {
        this.dataFechamento = dataFechamento;
    }

    public BigDecimal getSaldoFinal() {
        return saldoFinal;
    }

    public void setSaldoFinal(BigDecimal saldoFinal) {
        this.saldoFinal = saldoFinal;
    }

    public String getUsuarioUtilizando() {
        return usuarioUtilizando;
    }

    public void setUsuarioUtilizando(String usuarioUtilizando) {
        this.usuarioUtilizando = usuarioUtilizando;
    }

    public Tesouraria getTesouraria() {
        return tesouraria;
    }

    public void setTesouraria(Tesouraria tesouraria) {
        this.tesouraria = tesouraria;
    }

    public List<CaixaMovimento> getMovimentacoesCaixa() {
        return movimentacoesCaixa;
    }

    public void setMovimentacoesCaixa(List<CaixaMovimento> movimentacoesCaixa) {
        this.movimentacoesCaixa = movimentacoesCaixa;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    public void validarPedidoEstaoFinalizadosOuCanceladosParaFechamento() {
        if (this.getPedidos().stream()
                .anyMatch(p -> !p.getStatusPedido().equals(StatusPedido.FINALIZADO)
                        && !p.getStatusPedido().equals(StatusPedido.CANCELADO))) {
            throw new ConflictTesourariaException("Não é possível fechar: há pedidos pendentes.");
        }
    }
}
