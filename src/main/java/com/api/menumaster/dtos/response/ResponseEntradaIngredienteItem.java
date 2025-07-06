package com.api.menumaster.dtos.response;

import java.math.BigDecimal;
import java.util.UUID;

public record ResponseEntradaIngredienteItem(UUID idIngrediente,
                                             BigDecimal qtdEntrada,
                                             BigDecimal valorCusto) {
}
