package com.api.menumaster.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record RequestEntradaNotaIngredienteDto(@NotNull LocalDate dataEntrada,
                                               @NotBlank String numeroNota,
                                               @NotNull Integer serieNota,
                                               String observacao,
                                               @NotNull List<RequestEntradaIngredienteItem> itens) {
}
