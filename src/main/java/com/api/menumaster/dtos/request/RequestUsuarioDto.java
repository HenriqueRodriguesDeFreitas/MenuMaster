package com.api.menumaster.dtos.request;

import jakarta.validation.constraints.NotBlank;

public record RequestUsuarioDto(@NotBlank String nome,
                                @NotBlank String senha,
                                @NotBlank String role) {
}
