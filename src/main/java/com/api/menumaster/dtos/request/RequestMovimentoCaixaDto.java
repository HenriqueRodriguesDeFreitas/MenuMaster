package com.api.menumaster.dtos.request;

import com.api.menumaster.model.enums.TipoMovimento;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record RequestMovimentoCaixaDto(@NotNull BigDecimal valor,
                                       @NotNull @Size(max = 200) String descricao) {
}
