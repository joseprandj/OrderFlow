package com.github.joseprandj.OrderFlow_Pedido.exception;

import java.util.UUID;

public class PedidoNaoEncontradoException extends RuntimeException {

    public PedidoNaoEncontradoException(UUID id) {
        super("Pedido não encontrado: " + id);
    }
}
