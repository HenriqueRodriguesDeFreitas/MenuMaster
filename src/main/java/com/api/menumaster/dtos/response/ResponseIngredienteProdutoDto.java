package com.api.menumaster.dtos.response;

import java.math.BigDecimal;

public record ResponseIngredienteProdutoDto(String nomeIngrediente,
                                            BigDecimal quantidade) {
}
