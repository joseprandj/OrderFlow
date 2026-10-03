package com.github.joseprandj.OrderFlow_Produto.exception;

public class IntegracaoEstoqueException extends RuntimeException {

    public IntegracaoEstoqueException(Throwable causa) {
        super("Não foi possível criar o estoque do produto. Tente novamente mais tarde.", causa);
    }
}
