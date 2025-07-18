package com.api.menumaster.dtos.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record   RequestItemPedidoDto(@NotNull Long codigoProduto,
                                   @NotBlank @Digits(integer = 10, fraction = 2) BigDecimal qtdProduto) {
}
