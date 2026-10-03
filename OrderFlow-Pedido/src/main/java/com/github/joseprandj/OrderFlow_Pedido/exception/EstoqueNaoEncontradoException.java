package com.github.joseprandj.OrderFlow_Pedido.exception;

public class EstoqueNaoEncontradoException extends RuntimeException {

    public EstoqueNaoEncontradoException(String sku) {
        super("Estoque não encontrado para o SKU " + sku + ".");
    }
}
