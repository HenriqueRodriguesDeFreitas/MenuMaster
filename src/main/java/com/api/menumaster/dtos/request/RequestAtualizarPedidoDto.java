package com.api.menumaster.dtos.request;

import com.api.menumaster.model.enums.StatusPedido;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RequestAtualizarPedidoDto(
        @Schema(description = "Insira aqui o número da mesa a qual o pedido pertence", example = "1")
        Integer mesa,
        @Schema(description = "Insira aqui o nome do cliente a qual pedido pertence", example = "Paulo")
        @NotBlank String nomeCliente,
        @Schema(description = "Insira aqui o endereço caso pedido seja para entrega", example = "Av. Ernesto Giornot. n°1")
        @NotBlank String endereco,
        @Schema(description = "Insira aqui o número de contato do cliente", example = "00000000000")
        @NotBlank String contato,
        @Schema(description = "Insira aqui o novo status do pedido", example = "AGUARDANDO, FINALIZADO OU CANCELADO")
        StatusPedido status,
        @Schema(description = "Insira aqui uma observação", example = "Retire o queijo do hamburguer")
        String observacao,
        @NotNull List<RequestItemPedidoDto> itens) {
}
