package com.api.menumaster.dtos.request;

import com.api.menumaster.model.enums.UnidadeMedida;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record RequestIngredienteDto(@NotBlank @Size(max = 30) String nome,
                                    @Size(max = 250) String descricao,
                                    @Digits(integer = 8, fraction = 2) BigDecimal precoCusto,
                                    @Digits(integer = 8, fraction = 2) BigDecimal precoVenda,
                                    @NotNull boolean isAdicional,
                                    @NotNull UnidadeMedida unidadeMedida,
                                    @NotNull boolean controlarEstoque
) {
}
