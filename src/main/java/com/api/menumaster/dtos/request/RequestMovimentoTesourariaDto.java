package com.api.menumaster.dtos.request;

import com.api.menumaster.model.enums.FormaPagamento;
import com.api.menumaster.model.enums.TipoMovimento;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record RequestMovimentoTesourariaDto(
        @NotNull FormaPagamento formaPagamento,
        @NotNull @Digits(integer = 10, fraction = 2) BigDecimal valor,
        @NotNull @Size(max = 200) String descricao) {
}
