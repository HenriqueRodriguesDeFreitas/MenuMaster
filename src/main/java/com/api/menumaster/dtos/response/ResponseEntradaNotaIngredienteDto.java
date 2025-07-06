package com.api.menumaster.dtos.response;

import com.api.menumaster.dtos.request.RequestEntradaIngredienteItem;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ResponseEntradaNotaIngredienteDto(String fornecedor,
                                                LocalDate dataEntrada, String numeroNota,
                                                Integer serieNota, String observacao,
                                                List<ResponseEntradaIngredienteItem> itens,
                                                BigDecimal valorTotal) {
}
