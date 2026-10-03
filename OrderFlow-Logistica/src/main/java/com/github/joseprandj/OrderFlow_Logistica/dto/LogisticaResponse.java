package com.github.joseprandj.OrderFlow_Logistica.dto;

import com.github.joseprandj.OrderFlow_Logistica.model.Logistica;
import com.github.joseprandj.OrderFlow_Logistica.model.StatusLogistica;

import java.time.LocalDateTime;
import java.util.UUID;

public record LogisticaResponse(
        UUID id,
        UUID idPedido,
        StatusLogistica status,
        String ocorrencia,
        LocalDateTime dataHoraCriacao,
        LocalDateTime dataHoraAlteracao
) {

    public static LogisticaResponse from(Logistica logistica) {
        return new LogisticaResponse(
                logistica.getId(),
                logistica.getIdPedido(),
                logistica.getStatus(),
                logistica.getOcorrencia(),
                logistica.getDataHoraCriacao(),
                logistica.getDataHoraAlteracao()
        );
    }
}
