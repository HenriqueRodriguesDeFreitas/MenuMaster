package com.api.menumaster.dtos.response;

import com.api.menumaster.model.enums.FormaPagamento;
import com.api.menumaster.model.enums.TipoMovimento;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ResponseMovimentoCaixaDto(String usuarioMovimento,
                                        @JsonFormat(pattern = "dd/MM/yyyy hh:mm:ss") LocalDateTime dataMovimento,
                                        @Digits(integer = 10, fraction = 2) BigDecimal valor,
                                        TipoMovimento tipoMovimento,
                                        FormaPagamento formaPagamento,
                                        String descricao) {
}
