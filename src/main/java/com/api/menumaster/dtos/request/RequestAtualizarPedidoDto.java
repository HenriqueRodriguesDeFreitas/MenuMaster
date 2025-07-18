package com.api.menumaster.dtos.request;

import com.api.menumaster.model.enums.StatusPedido;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RequestAtualizarPedidoDto(Integer mesa,
                                        @NotBlank String nomeCliente,
                                        @NotBlank String endereco,
                                        @NotBlank String contato,
                                        StatusPedido status,
                                        String observacao,
                                        @NotNull List<RequestItemPedidoDto> itens) {
}
