package com.github.joseprandj.OrderFlow_Pedido.dto;

import com.github.joseprandj.OrderFlow_Pedido.model.Pedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PedidoResponse(
        UUID id,
        UUID idCliente,
        List<ItemPedidoResponse> itens,
        BigDecimal valorTotal,
        UUID idFinanceiro,
        UUID idLogistica,
        LocalDateTime dataHoraCriacao,
        LocalDateTime dataHoraAlteracao
) {

    public static PedidoResponse from(Pedido pedido) {
        return new PedidoResponse(
                pedido.getId(),
                pedido.getIdCliente(),
                pedido.getItens().stream().map(ItemPedidoResponse::from).toList(),
                pedido.getValorTotal(),
                pedido.getIdFinanceiro(),
                pedido.getIdLogistica(),
                pedido.getDataHoraCriacao(),
                pedido.getDataHoraAlteracao()
        );
    }
}
