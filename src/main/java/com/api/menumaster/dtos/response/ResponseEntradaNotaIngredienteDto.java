package com.api.menumaster.dtos.response;

import com.api.menumaster.dtos.request.RequestEntradaIngredienteItem;

import java.time.LocalDate;
import java.util.List;

public record ResponseEntradaNotaIngredienteDto(LocalDate dataEntrada, String numeroNota,
                                                Integer serieNota, String observacao,
                                                List<RequestEntradaIngredienteItem> itens) {
}
