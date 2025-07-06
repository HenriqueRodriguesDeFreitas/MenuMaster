package com.api.menumaster.dtos.request;

import java.math.BigDecimal;
import java.util.UUID;

public record RequestEntradaIngredienteItem(Integer codigoIngrediente,
                                            BigDecimal qtdEntrada,
                                            BigDecimal valorCusto) {
}
