package com.api.menumaster.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ResponseItemPedidoDto(
        @Schema(description = "Nome do produto relacionado ao pedido", example = "Hamburguer Simples")
        String nomeProduto,
        @Schema(description = "Quantidade do produto relacionado ao pedido", example = "3")
        @Digits(integer = 10, fraction = 2) BigDecimal qtdProduto,
        @Schema(description = "Preço do produto relacionado ao pedido", example = "30.00")
        @Digits(integer = 10, fraction = 2) BigDecimal precoUnitario) {
}
