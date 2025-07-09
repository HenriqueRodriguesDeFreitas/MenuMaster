package com.api.menumaster.model;

import com.api.menumaster.exception.custom.EntityNotFoundException;
import com.api.menumaster.repository.EntradaIngredienteRepository;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "entrada_ingrediente")
public class EntradaIngrediente {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "data_entrada", nullable = false)
    private LocalDate dataEntrada;
    @Column(name = "numero_nota", nullable = false, length = 50)
    private String numeroNota;
    @Column(name = "serie_nota", nullable = false, length = 1)
    private Integer serieNota;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "fornecedor_id")
    private Fornecedor fornecedor;

    @Column(name = "valor_total")
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Column(length = 250)
    private String observacao;

    @OneToMany(mappedBy = "entrada", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EntradaIngredienteItem> itens = new ArrayList<>();


    public EntradaIngrediente() {
    }


    public EntradaIngrediente(LocalDate dataEntrada, String numeroNota,
                              Integer serieNota, Fornecedor fornecedor,
                              BigDecimal valorTotal, String observacao) {
        this.dataEntrada = dataEntrada;
        this.numeroNota = numeroNota;
        this.serieNota = serieNota;
        this.fornecedor = fornecedor;
        this.valorTotal = valorTotal;
        this.observacao = observacao;
    }

    public UUID getId() {
        return id;
    }

    public LocalDate getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(LocalDate dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public String getNumeroNota() {
        return numeroNota;
    }

    public void setNumeroNota(String numeroNota) {
        this.numeroNota = numeroNota;
    }

    public Integer getSerieNota() {
        return serieNota;
    }

    public void setSerieNota(Integer serieNota) {
        this.serieNota = serieNota;
    }

    public Fornecedor getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Fornecedor fornecedor) {
        this.fornecedor = fornecedor;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public List<EntradaIngredienteItem> getItens() {
        return itens;
    }

    public void setItens(List<EntradaIngredienteItem> itens) {
        this.itens = itens;
    }

    public void addItemIngrediente(Ingrediente ingrediente, BigDecimal quantiade, BigDecimal custoUnitario) {
        EntradaIngredienteItem item = new EntradaIngredienteItem(this, ingrediente, quantiade, custoUnitario);
        itens.add(item);

    }

    public void calcularTotalNota() {
        this.valorTotal = itens.stream()
                .map(item -> item.getCustoUnitario().multiply(item.getQuantidade()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean verificarSeNotaPertenceAoFornecedor(EntradaIngredienteRepository entradaRepository) {
        if (this.fornecedor == null) {
            throw new EntityNotFoundException("Insira um fornecedor");
        }
        if (this.numeroNota == null) {
            throw new EntityNotFoundException("Digite o número da nota");
        }

        return entradaRepository.existsByFornecedorIdAndNumeroNota(this.fornecedor.getId(), this.numeroNota);
    }
}
