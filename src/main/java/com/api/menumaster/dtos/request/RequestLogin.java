package com.api.menumaster.dtos.request;

import jakarta.validation.constraints.NotBlank;

public record RequestLogin(@NotBlank String usuario, @NotBlank String senha) {
}
