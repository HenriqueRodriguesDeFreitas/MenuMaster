package com.api.menumaster.model;

public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "data_emissao", nullable = false)
    private LocalDateTime dataEmissao = LocalDateTime.now();

    @Column(name = "data_edicao")
    private LocalDateTime dataEdicao;

    @Column(name = "total_pedido", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPedido;

    @Column(nullable = false)
    private Integer mesa;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "statuspedido", name = "status_pedido")
    private StatusPedido statusPedido;

    @Column(name = "nome_cliente", length = 250, nullable = false)
    private String nomeCliente;
    @Column(length = 250, nullable = false)
    private String endereco;

    @Column(length = 11, nullable = false)
    private String contato;

    @Column(length = 250)
    private String observacao;

    @OneToMany(mappedBy = "pedido", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<ItemPedido> itensAssociados = new ArrayList<>();


    public Pedido() {
    }

    public Pedido(LocalDateTime dataEdicao, BigDecimal totalPedido,
                  String nomeCliente, String endereco, String contato, String observacao) {
        this.dataEdicao = dataEdicao;
        this.totalPedido = totalPedido;
        this.statusPedido = StatusPedido.AGUARDANDO;
        this.nomeCliente = nomeCliente;
        this.endereco = endereco;
        this.contato = contato;
        this.observacao = observacao;
    }

    public UUID getId() {
        return id;
    }

    public LocalDateTime getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(LocalDateTime dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public LocalDateTime getDataEdicao() {
        return dataEdicao;
    }

    public void setDataEdicao(LocalDateTime dataEdicao) {
        this.dataEdicao = dataEdicao;
    }

    public BigDecimal getTotalPedido() {
        return totalPedido;
    }

    public void setTotalPedido(BigDecimal totalPedido) {
        this.totalPedido = totalPedido;
    }

    public StatusPedido getStatusPedido() {
        return statusPedido;
    }

    public void setStatusPedido(StatusPedido statusPedido) {
        this.statusPedido = statusPedido;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public List<ItemPedido> getItensAssociados() {
        return itensAssociados;
    }

    public void setItensAssociados(List<ItemPedido> itensAssociados) {
        this.itensAssociados = itensAssociados;
    }

    public Integer getMesa() {
        return mesa;
    }

    public void setMesa(Integer mesa) {
        this.mesa = mesa;
    }

    public void ajustarQuantidadeParaUnidade() {
        itensAssociados.forEach(i -> {
            if (i.getQuantidadeProduto().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Quantidade precisa ser preenchida com valor maior que zero");
            }
            if (i.getProduto() == null) {
                throw new ConflictEntityException("Produtos precisan ser adicionados no pedido");
            }
            if (i.getProduto().getUnidadeMedida().equals(UnidadeMedida.UN)) {
                i.setQuantidadeProduto(i.getQuantidadeProduto().setScale(0, RoundingMode.DOWN));
            }
        });
    }

    public void calcularTotalPedido() {
        this.totalPedido = itensAssociados.stream()
                .map(i -> i.getProduto().getPrecoVenda().multiply(i.getQuantidadeProduto()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void mudarStatusPedido(StatusPedido statusPedido) {
        if (this.statusPedido != statusPedido) {
            this.setStatusPedido(statusPedido);
        }
    }


}
