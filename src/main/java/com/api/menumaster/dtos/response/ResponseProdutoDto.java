package com.api.menumaster.dtos.response;

import java.math.BigDecimal;
import java.util.List;

public record ResponseProdutoDto(String nome,
                                 Long codigoProduto,
                                 String descricao,
                                 @NotNull List<RequestIngredienteProdutoDto> ingredientes) {
}
