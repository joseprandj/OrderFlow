package com.github.joseprandj.OrderFlow_Pedido.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record ItemPedidoRequest(
        @NotNull(message = "é obrigatório")
        UUID idProduto,

        @NotNull(message = "é obrigatório")
        @Positive(message = "deve ser maior que zero")
        Integer quantidade
) {
}
