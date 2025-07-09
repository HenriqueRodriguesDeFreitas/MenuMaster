package com.api.menumaster.dtos.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RequestUpdateItensEntradaIngredienteDto(String observacao,
                                                      @NotNull List<RequestEntradaIngredienteItem> itens) {
}
