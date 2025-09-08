package com.api.menumaster.dtos.request;

import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;

public record RequestAbrirCaixaDto(@Digits(integer = 10, fraction = 2) BigDecimal saldoInicial) {
}
