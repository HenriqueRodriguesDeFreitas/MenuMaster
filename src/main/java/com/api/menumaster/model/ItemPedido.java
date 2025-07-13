package com.api.menumaster.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "item_pedido")
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(precision = 10, scale = 3)
    private BigDecimal quantidadeProduto;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "produto_id")
    private Produto produto;

    public ItemPedido() {
    }

    public ItemPedido(BigDecimal quantidadeProduto, Pedido pedido, Produto produto) {
        this.quantidadeProduto = quantidadeProduto;
        this.pedido = pedido;
        this.produto = produto;
    }

    public UUID getId() {
        return id;
    }

    public BigDecimal getQuantidadeProduto() {
        return quantidadeProduto;
    }

    public void setQuantidadeProduto(BigDecimal quantidadeProduto) {
        this.quantidadeProduto = quantidadeProduto;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }
}

