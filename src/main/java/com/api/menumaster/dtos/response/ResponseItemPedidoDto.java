package com.api.menumaster.dtos.response;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ResponseItemPedidoDto(String nomeProduto,
                                    @Digits(integer = 10, fraction = 2) BigDecimal qtdProduto,
                                    @Digits(integer = 10, fraction = 2) BigDecimal precoUnitario) {
}
