package com.api.menumaster.dtos.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RequestIngredienteProdutoDto(@NotNull Integer codigoIngrediente,
                                           @NotNull BigDecimal quantidade) {
}
