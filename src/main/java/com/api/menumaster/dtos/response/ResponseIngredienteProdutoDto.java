package com.api.menumaster.dtos.response;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ResponseIngredienteProdutoDto(@NotNull Integer codigoProduto,
                                            @NotNull BigDecimal quantidade) {
}
