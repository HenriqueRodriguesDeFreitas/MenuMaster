package com.api.menumaster.model;

import com.api.menumaster.model.enums.UnidadeMedida;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ingrediente")
public class Ingrediente {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "codigo", nullable = false)
    private Integer codigo;

    @Column(length = 30, nullable = false)
    private String nome;

    @Column(length = 250)
    private String descricao;

    @Column(precision = 10, scale = 3)
    private BigDecimal estoque;

    @Column(name = "preco_custo", precision = 10, scale = 2, nullable = false)
    private BigDecimal precoCusto;

    @Column(name = "preco_venda", precision = 10, scale = 2)
    private BigDecimal precoVenda;

    @Column(name = "is_ativo", nullable = false)
    private boolean isAtivo;

    @Column(name = "is_adicional", nullable = false)
    private boolean isAdicional;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "unidademedida", name = "unidade_medida")
    private UnidadeMedida unidadeMedida;

    @Column(name = "controlar_estoque")
    private boolean controlarEstoque;

    @OneToMany(mappedBy = "ingrediente")
    private List<EntradaIngredienteItem> entradaItems = new ArrayList<>();

    public Ingrediente() {
    }

    public Ingrediente(String nome, String descricao, BigDecimal estoque,
                       BigDecimal precoCusto, BigDecimal precoVenda, boolean isAtivo,
                       boolean isAdicional, UnidadeMedida unidadeMedida, boolean controlarEstoque) {
        this.nome = nome;
        this.descricao = descricao;
        this.estoque = estoque;
        this.precoCusto = precoCusto;
        this.precoVenda = precoVenda;
        this.isAtivo = isAtivo;
        this.isAdicional = isAdicional;
        this.unidadeMedida = unidadeMedida;
        this.controlarEstoque = controlarEstoque;
    }

    public UUID getId() {
        return id;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getEstoque() {
        return estoque;
    }

    public void setEstoque(BigDecimal estoque) {
        this.estoque = estoque;
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

    public boolean isAdicional() {
        return isAdicional;
    }

    public void setAdicional(boolean adicional) {
        isAdicional = adicional;
    }

    public UnidadeMedida getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public boolean isControlarEstoque() {
        return controlarEstoque;
    }

    public void setControlarEstoque(boolean controlarEstoque) {
        this.controlarEstoque = controlarEstoque;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public List<EntradaIngredienteItem> getEntradaItems() {
        return entradaItems;
    }

    public void setEntradaItems(List<EntradaIngredienteItem> entradaItems) {
        this.entradaItems = entradaItems;
    }
}
