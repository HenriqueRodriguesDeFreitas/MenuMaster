package com.api.menumaster.dtos.response;

import com.api.menumaster.model.enums.StatusPedido;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ResponsePedidoDto(
        @Schema(description = "Id do pedido", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,
        @Schema(description = "Mesa a qual o pedido pertence", example = "1")
        Integer mesa,
        @Schema(description = "Status do pedido", example = "AGUARDANDO")
        StatusPedido status,
        @Schema(description = "Data de emissão do pedido", example = "16-09-2025 00:00:00")
        @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime emissao,
        @Schema(description = "Data de edição do pedido", example = "16-09-2025 00:30:00")
        @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime editado,
        @Schema(description = "Nome do cliente", example = "Paulo")
        String nomeCliente,
        @Schema(description = "Endereço do cliente", example = "Av. Ernesto Giorno, N°1")
        String endereco,
        @Schema(description = "Contato do cliente", example = "00000000000")
        String contato,
        @Schema(description = "Observação do pedido", example = "Corte o hamburguer ao meio")
        String observacao,
        @Schema(description = "Valor total do pedido", example = "100.00")
        BigDecimal totalPedido,
        @Schema(description = "itens do pedido")
        List<ResponseItemPedidoDto> itens) {
}
