package com.github.joseprandj.OrderFlow_Pedido.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

import static org.springframework.util.StringUtils.trimWhitespace;

/**
 * O cliente é identificado pelo {@code cpfCnpj} (somente dígitos); o pedido armazena o {@code idCliente}
 * obtido no domínio Cliente.
 */
public record PedidoRequest(
        @NotBlank(message = "é obrigatório")
        String cpfCnpj,

        @NotEmpty(message = "deve possuir ao menos um item")
        List<@Valid @NotNull(message = "não pode ser nulo") ItemPedidoRequest> itens
) {

    public PedidoRequest {
        cpfCnpj = trimWhitespace(cpfCnpj);
    }
}
