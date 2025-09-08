package com.api.menumaster.dtos.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ResponseCaixaDto(UUID idCaixa,
                               @Digits(integer = 10, fraction = 2) BigDecimal saldoInicial,
                               @Digits(integer = 10, fraction = 2) BigDecimal saldoFinal,
                               @JsonFormat(pattern = "dd/MM/yyyy hh:mm:ss") LocalDateTime dataAbertura,
                               @JsonFormat(pattern = "dd/MM/yyyy hh:mm:ss") LocalDateTime dataFechamento,
                               String usuarioUtilizando) {
}
