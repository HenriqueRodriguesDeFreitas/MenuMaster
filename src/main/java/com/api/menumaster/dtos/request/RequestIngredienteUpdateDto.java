package com.api.menumaster.dtos.request;

import com.api.menumaster.model.enums.UnidadeMedida;

import java.math.BigDecimal;
import java.util.UUID;

public record RequestIngredienteUpdateDto(
                                          Integer codigo,
                                          String nome,
                                          String descricao,
                                          BigDecimal estoque,
                                          BigDecimal precoCusto,
                                          BigDecimal precoVenda,
                                          boolean isAtivo,
                                          boolean isAdicional,
                                          UnidadeMedida unidadeMedida,
                                          boolean controlarEstoque
) {
}
