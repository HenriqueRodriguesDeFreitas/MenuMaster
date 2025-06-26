package com.api.menumaster.dtos.response;

import java.util.UUID;

public record ResponseFornecedorDto(UUID id,
                                    String razaoSocial,
                                    String nomeFantasia,
                                    String cnpj,
                                    String inscricaoEstadual,
                                    String endereco,
                                    String contato,
                                    boolean isAtivo) {
}
