package com.api.menumaster.dtos.response;

import com.api.menumaster.dtos.request.RequestIngredienteProdutoDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ResponseProdutoDto(@NotBlank String nome,
                                 @NotNull Long codigoProduto,
                                 String descricao,
                                 @NotNull List<RequestIngredienteProdutoDto> ingredientes) {
}
