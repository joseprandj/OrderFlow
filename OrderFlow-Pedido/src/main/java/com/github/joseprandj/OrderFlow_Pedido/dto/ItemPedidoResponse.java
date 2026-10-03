package com.github.joseprandj.OrderFlow_Pedido.dto;

import com.github.joseprandj.OrderFlow_Pedido.model.ItemPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ItemPedidoResponse(
        UUID id,
        UUID idProduto,
        String sku,
        BigDecimal preco,
        int quantidade,
        BigDecimal valorTotal,
        LocalDateTime dataHoraCriacao,
        LocalDateTime dataHoraAlteracao
) {

    public static ItemPedidoResponse from(ItemPedido item) {
        return new ItemPedidoResponse(
                item.getId(),
                item.getIdProduto(),
                item.getSku(),
                item.getPreco(),
                item.getQuantidade(),
                item.getValorTotal(),
                item.getDataHoraCriacao(),
                item.getDataHoraAlteracao()
        );
    }
}
