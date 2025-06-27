package com.api.menumaster.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RequestFornecedorUpdateDto(@NotBlank String razaoSocial,
                                         @NotBlank String nomeFantasia,
                                         @NotBlank @Size(min = 14, max = 14) String cnpj,
                                         @NotBlank @Size(min = 9, max = 9) String inscricaoEstadual,
                                         String endereco,
                                         String contato,
                                         boolean isAtivo) {
}
