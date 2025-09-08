package com.api.menumaster.dtos.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ResponseAbrirCaixaDto(String usuarioUtilizando,
                                    @JsonFormat(pattern = "dd/MM/yyyy hh:mm:ss") LocalDateTime dataAbertura,
                                    @Digits(integer = 10, fraction = 2) BigDecimal saldoInicial) {
}
