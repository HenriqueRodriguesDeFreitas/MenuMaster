package com.api.menumaster.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record RequestAtualizarProdutoDto(@NotNull Long codigoProduto,
                                         @NotBlank @Size(max = 50) String nome,
                                         @Size(max = 250) String descricao,
                                         @NotNull List<RequestIngredienteProdutoDto> ingredientes) {
}
