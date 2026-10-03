package com.github.joseprandj.OrderFlow_Pedido.exception;

public class EstoqueInsuficienteException extends RuntimeException {

    public EstoqueInsuficienteException(String sku, int disponivel, int solicitado) {
        super("Estoque insuficiente para o SKU " + sku + ": disponível " + disponivel + ", solicitado " + solicitado + ".");
    }
}
