package com.github.joseprandj.OrderFlow_Pedido.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ItemPedidoQuantidadeRequest(
        @NotNull(message = "é obrigatório")
        @Positive(message = "deve ser maior que zero")
        Integer quantidade
) {
}
