package com.github.joseprandj.OrderFlow_Estoque.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record EstoqueQuantidadeRequest(
        @NotNull(message = "é obrigatório")
        @PositiveOrZero(message = "deve ser maior ou igual a zero")
        Integer quantidade
) {
}
