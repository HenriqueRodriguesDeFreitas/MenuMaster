package com.api.menumaster.dtos.response;

import com.api.menumaster.model.enums.UnidadeMedida;

import java.math.BigDecimal;

public record ResponseIngredienteDto(String nome,
                                     String descricao,
                                     BigDecimal precoCusto,
                                     BigDecimal precoVenda,
                                     boolean isAdicional,
                                     UnidadeMedida unidadeMedida,
                                     boolean controlarEstoque
) {
}
