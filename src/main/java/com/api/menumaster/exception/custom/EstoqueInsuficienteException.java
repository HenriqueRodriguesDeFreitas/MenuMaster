package com.api.menumaster.exception.custom;

import java.math.BigDecimal;

public class EstoqueInsuficienteException extends RuntimeException {
    public EstoqueInsuficienteException(String produtoNome,
                                        String nomeIngredienteDoProduto,
                                        BigDecimal quantidadeRequeridaDoIngrediente,
                                        BigDecimal estoqueDisponivelDoIngrediente) {
        super(String.format("Produto: %s impossibilitado para venda, " +
                        "seu ingrediente: %s não possui estoque para tal operação. " +
                        "Quantidade requerida do ingrediente: %.2f, estoque disponivel: %.2f", produtoNome,
                nomeIngredienteDoProduto, quantidadeRequeridaDoIngrediente, estoqueDisponivelDoIngrediente));
    }
}
