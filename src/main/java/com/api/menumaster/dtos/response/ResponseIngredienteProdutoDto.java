package com.api.menumaster.dtos.response;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ResponseIngredienteProdutoDto(String nomeIngrediente,
                                            BigDecimal quantidade) {
}
