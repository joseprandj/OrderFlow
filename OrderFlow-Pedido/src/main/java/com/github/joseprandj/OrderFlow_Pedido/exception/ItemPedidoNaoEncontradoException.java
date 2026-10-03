package com.github.joseprandj.OrderFlow_Pedido.exception;

import java.util.UUID;

public class ItemPedidoNaoEncontradoException extends RuntimeException {

    public ItemPedidoNaoEncontradoException(UUID idItem) {
        super("Item não encontrado no pedido: " + idItem);
    }
}
