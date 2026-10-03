package com.github.joseprandj.OrderFlow_Estoque.exception;

public class SkuJaCadastradoException extends RuntimeException {

    public SkuJaCadastradoException(String sku) {
        super("Já existe um estoque cadastrado para o SKU " + sku + ".");
    }
}
