package com.github.joseprandj.OrderFlow_Financeiro.dto;

import com.github.joseprandj.OrderFlow_Financeiro.model.StatusFinanceiro;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record FinanceiroAtualizacaoRequest(
        @NotNull(message = "é obrigatório")
        @PositiveOrZero(message = "deve ser maior ou igual a zero")
        @Digits(integer = 17, fraction = 2, message = "deve possuir no máximo 2 casas decimais")
        BigDecimal valor,

        @NotNull(message = "é obrigatório")
        StatusFinanceiro status
) {
}
