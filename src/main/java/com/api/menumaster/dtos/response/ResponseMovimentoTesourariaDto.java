package com.api.menumaster.dtos.response;

import com.api.menumaster.model.enums.FormaPagamento;
import com.api.menumaster.model.enums.TipoMovimento;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ResponseMovimentoTesourariaDto(@JsonFormat(pattern = "dd/MM/yyyy hh:mm:ss")
                                             LocalDateTime dataMovimento,
                                             String tipoMovimento,
                                             String formaPagamento,
                                             @Digits(integer = 10, fraction = 2) BigDecimal valor,
                                             String descricao,
                                             String usuarioMovimento) {
}
