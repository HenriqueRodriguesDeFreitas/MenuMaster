package com.api.menumaster.model;

import com.api.menumaster.model.enums.UnidadeMedida;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "produto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 50, nullable = false)
    private String nome;

    @Column(name = "codigo_produto", nullable = false)
    private Long codigoProduto;

    @Column(length = 250)
    private String descricao;

    @Column(name = "quantidade_vendida", precision = 10, scale = 3)
    private BigDecimal quantidadeVendida;

    @Column(name = "preco_custo", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoCusto;

    @Column(name = "preco_venda", precision = 10, scale = 2)
    private BigDecimal precoVenda;

    @Column(name = "is_ativo")
    private boolean isAtivo;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "unidademedida", name = "unidade_medida")
    private UnidadeMedida unidadeMedida;

    @OneToMany(mappedBy = "produto", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<IngredienteProduto> ingredientesAssociados = new ArrayList<>();

    @OneToMany(mappedBy = "produto", cascade = {CascadeType.ALL, CascadeType.MERGE}, orphanRemoval = true)
    private List<ItemPedido> itensProduto = new ArrayList<>();


    public Produto() {
    }

    public Produto(String nome, Long codigoProduto, String descricao,
                   BigDecimal quantidadeVendida, BigDecimal precoCusto, BigDecimal precoVenda,
                   boolean isAtivo) {
        this.nome = nome;
        this.codigoProduto = codigoProduto;
        this.descricao = descricao;
        this.quantidadeVendida = quantidadeVendida;
        this.precoCusto = precoCusto;
        this.precoVenda = precoVenda;
        this.isAtivo = isAtivo;
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Long getCodigoProduto() {
        return codigoProduto;
    }

    public void setCodigoProduto(Long codigoProduto) {
        this.codigoProduto = codigoProduto;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getQuantidadeVendida() {
        return quantidadeVendida;
    }

    public void setQuantidadeVendida(BigDecimal quantidadeVendida) {
        this.quantidadeVendida = quantidadeVendida;
    }

    public BigDecimal getPrecoCusto() {
        return precoCusto;
    }

    public void setPrecoCusto(BigDecimal precoCusto) {
        this.precoCusto = precoCusto;
    }

    public BigDecimal getPrecoVenda() {
        return precoVenda;
    }

    public void setPrecoVenda(BigDecimal precoVenda) {
        this.precoVenda = precoVenda;
    }

    public boolean isAtivo() {
        return isAtivo;
    }

    public void setAtivo(boolean ativo) {
        isAtivo = ativo;
    }

    public List<ItemPedido> getItensProduto() {
        return itensProduto;
    }

    public UnidadeMedida getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public void setItensProduto(List<ItemPedido> itensProduto) {
        this.itensProduto = itensProduto;
    }

    public List<IngredienteProduto> getIngredientesAssociados() {
        return ingredientesAssociados;
    }

    public void setIngredientesAssociados(List<IngredienteProduto> ingredientesAssociados) {
        this.ingredientesAssociados = ingredientesAssociados;
    }

    public void calcularPrecoCusto() {
        this.precoCusto = ingredientesAssociados.stream()
                .map(i -> i.getIngrediente().getPrecoCusto().multiply(i.getQuantidade()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public void calcularPrecoVenda() {
        this.precoVenda = precoCusto.multiply(BigDecimal.valueOf(1.1))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
