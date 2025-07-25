package com.api.menumaster.dtos.response;

import java.util.UUID;

public record ResponseUsuarioDto(
        UUID id,
        String nome,
        String senha,
        String role) {
}
