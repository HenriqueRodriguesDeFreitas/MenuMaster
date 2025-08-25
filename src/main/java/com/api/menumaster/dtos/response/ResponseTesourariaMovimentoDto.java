package com.api.menumaster.dtos.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ResponseTesourariaMovimentoDto(
        UUID idMovimento,
        @JsonFormat(pattern = "dd/MM/yyyy hh:mm:ss")
        LocalDateTime dataMovimento,
        String tipoMovimento,
        String formaPagamento,
        @Digits(integer = 10, fraction = 2) BigDecimal valor,
        String descricao,
        String usuarioMovimento) {
}
