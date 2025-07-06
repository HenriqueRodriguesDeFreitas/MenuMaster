package com.api.menumaster.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "entrada_ingrediente_itens")
public class EntradaIngredienteItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "entrada_id")
    private EntradaIngrediente entrada;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "ingrediente_id")
    private Ingrediente ingrediente;

    private BigDecimal quantidade;
    private BigDecimal custoUnitario;

    public EntradaIngredienteItem(){}

    public EntradaIngredienteItem(EntradaIngrediente entrada,
                                  Ingrediente ingrediente,
                                  BigDecimal quantidade,
                                  BigDecimal custoUnitario) {
        this.entrada = entrada;
        this.ingrediente = ingrediente;
        this.quantidade = quantidade;
        this.custoUnitario = custoUnitario;
    }

    public UUID getId() {
        return id;
    }

    public EntradaIngrediente getEntrada() {
        return entrada;
    }

    public void setEntrada(EntradaIngrediente entrada) {
        this.entrada = entrada;
    }

    public Ingrediente getIngrediente() {
        return ingrediente;
    }

    public void setIngrediente(Ingrediente ingrediente) {
        this.ingrediente = ingrediente;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getCustoUnitario() {
        return custoUnitario;
    }

    public void setCustoUnitario(BigDecimal custoUnitario) {
        this.custoUnitario = custoUnitario;
    }


}
