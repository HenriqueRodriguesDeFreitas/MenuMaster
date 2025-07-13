package com.api.menumaster.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "ingrediente_produto")
public class IngredienteProduto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(precision = 10, scale = 3, nullable = false)
    private BigDecimal quantidade;

    @ManyToOne
    @JoinColumn(name = "ingrediente_id", nullable = false)
    private Ingrediente ingrediente;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produto produto;

    public IngredienteProduto() {
    }

    public IngredienteProduto(BigDecimal quantidade,Ingrediente ingrediente, Produto produto) {
        this.quantidade = quantidade;
        this.ingrediente = ingrediente;
        this.produto = produto;
    }

    public UUID getId() {
        return id;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public Ingrediente getIngrediente() {
        return ingrediente;
    }

    public void setIngrediente(Ingrediente ingrediente) {
        this.ingrediente = ingrediente;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public void calcularCusto(){}
}
