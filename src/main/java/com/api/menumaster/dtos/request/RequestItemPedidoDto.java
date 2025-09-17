package com.api.menumaster.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RequestItemPedidoDto(
        @Schema(description = "Insira aqui o código do produto que deseja adicionar ao pedido", example = "1")
        @NotNull Long codigoProduto,
        @Schema(description = "Insira aqui a quantidade desejada do produto", example = "1")
        @NotBlank @Digits(integer = 10, fraction = 2) BigDecimal qtdProduto) {
}
