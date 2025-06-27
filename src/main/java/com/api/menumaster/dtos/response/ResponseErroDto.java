package com.api.menumaster.dtos.response;

import java.time.LocalDateTime;

public record ResponseErroDto(LocalDateTime localDateTime,
                              int httpsValue,
                              String erro,
                              String descricao) {
}
