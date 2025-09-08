package com.api.menumaster.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record RequestUserUpdateDto(@NotBlank String novoNome,
                                   @NotBlank String novaSenha,
                                   @NotNull Set<String> authorities) {
}
