package com.github.joseprandj.OrderFlow_Pedido.exception;

public class ItemDuplicadoException extends RuntimeException {

    public ItemDuplicadoException(String sku) {
        super("O pedido já possui um item para o SKU " + sku + ".");
    }
}
