package com.api.menumaster.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RequestCriarPedidoDto(Integer mesa,
                                    @NotBlank String nomeCliente,
                                    @NotBlank String endereco,
                                    @NotBlank String contato,
                                    String observacao,
                                    @NotNull List<RequestItemPedidoDto> itens) {
}
