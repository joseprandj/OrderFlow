package com.github.joseprandj.OrderFlow_Financeiro.dto;

import com.github.joseprandj.OrderFlow_Financeiro.model.Financeiro;
import com.github.joseprandj.OrderFlow_Financeiro.model.StatusFinanceiro;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record FinanceiroResponse(
        UUID id,
        UUID idPedido,
        BigDecimal valor,
        StatusFinanceiro status,
        LocalDateTime dataHoraCriacao,
        LocalDateTime dataHoraAlteracao
) {

    public static FinanceiroResponse from(Financeiro financeiro) {
        return new FinanceiroResponse(
                financeiro.getId(),
                financeiro.getIdPedido(),
                financeiro.getValor(),
                financeiro.getStatus(),
                financeiro.getDataHoraCriacao(),
                financeiro.getDataHoraAlteracao()
        );
    }
}
