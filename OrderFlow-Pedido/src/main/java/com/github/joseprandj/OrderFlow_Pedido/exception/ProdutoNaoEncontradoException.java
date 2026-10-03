package com.github.joseprandj.OrderFlow_Pedido.exception;

import java.util.UUID;

public class ProdutoNaoEncontradoException extends RuntimeException {

    public ProdutoNaoEncontradoException(UUID idProduto) {
        super("Produto não encontrado: " + idProduto);
    }
}
