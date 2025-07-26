package com.api.menumaster.dtos.request;

import com.api.menumaster.model.enums.FormaPagamento;
import com.api.menumaster.model.enums.TipoMovimento;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RequestTesourariaMovimentacaoDto(@NotNull TipoMovimento tipoMovimento,
                                               @NotNull FormaPagamento formaPagamento,
                                               @NotNull BigDecimal valor,
                                               String descricao) {
}
