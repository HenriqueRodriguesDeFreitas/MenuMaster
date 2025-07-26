package com.api.menumaster.dtos.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ResponseTesourariaDto(UUID id,
                                    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss") LocalDateTime dataAbertura,
                                    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")  LocalDateTime dataFechamento,
                                    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")  LocalDateTime dataReabertura,
                                    BigDecimal saldoInicial,
                                    BigDecimal saldoFinal,
                                    String usuarioAbertura,
                                    String usuarioFechamento,
                                    String usuarioReabertura){
}
