package com.api.menumaster.dtos.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RequestEntradaIngredienteItem(@NotNull Integer codigoIngrediente,
                                            @NotNull BigDecimal qtdEntrada,
                                            @NotNull BigDecimal valorCusto) {
}
