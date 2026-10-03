package com.github.joseprandj.OrderFlow_Produto.exception;

public class SkuJaCadastradoException extends RuntimeException {

    public SkuJaCadastradoException(String sku) {
        super("Já existe um produto cadastrado com o SKU " + sku + ".");
    }
}
