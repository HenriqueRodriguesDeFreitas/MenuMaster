package com.api.menumaster.dtos.response;

import com.api.menumaster.dtos.request.RequestItemPedidoDto;
import com.api.menumaster.model.enums.StatusPedido;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ResponsePedidoDto(UUID id,
                                Integer mesa,
                                StatusPedido status,
                                @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
                                LocalDateTime emissao,
                                @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
                                LocalDateTime editado,
                                String nomeCliente,
                                String endereco,
                                String contato,
                                String observacao,
                                BigDecimal totalPedido,
                                List<ResponseItemPedidoDto> itens) {
}
