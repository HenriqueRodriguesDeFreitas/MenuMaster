package com.api.menumaster.dtos.response;

import java.math.BigDecimal;
import java.util.UUID;

public record ResponseEntradaIngredienteItem(Integer codigoIngrediente,
                                             BigDecimal qtdEntrada,
                                             BigDecimal valorCusto) {
}
