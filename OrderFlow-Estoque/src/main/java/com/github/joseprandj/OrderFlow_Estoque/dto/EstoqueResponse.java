package com.github.joseprandj.OrderFlow_Estoque.dto;

import com.github.joseprandj.OrderFlow_Estoque.model.Estoque;

import java.time.LocalDateTime;
import java.util.UUID;

public record EstoqueResponse(
        UUID id,
        String sku,
        int quantidade,
        LocalDateTime dataHoraCriacao,
        LocalDateTime dataHoraAlteracao
) {

    public static EstoqueResponse from(Estoque estoque) {
        return new EstoqueResponse(
                estoque.getId(),
                estoque.getSku(),
                estoque.getQuantidade(),
                estoque.getDataHoraCriacao(),
                estoque.getDataHoraAlteracao()
        );
    }
}
