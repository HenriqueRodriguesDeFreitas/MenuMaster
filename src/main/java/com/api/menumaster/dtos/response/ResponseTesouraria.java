package com.api.menumaster.dtos.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ResponseTesouraria (UUID id,
                                  @JsonFormat(pattern = "dd/MM/yyyy") LocalDate dataAbertura,
                                  BigDecimal saldoInicial,
                                  BigDecimal saldoFinal,
                                  @JsonFormat(pattern = "dd/MM/yyyy")  LocalDate dataFechamento,
                                  String usuarioAbertura,
                                  String usuarioFechamento){
}
