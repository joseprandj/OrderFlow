package com.github.joseprandj.OrderFlow_Pedido.exception;

import java.util.UUID;

public class ProdutoInativoException extends RuntimeException {

    public ProdutoInativoException(UUID idProduto) {
        super("Produto inativo não pode ser utilizado no pedido: " + idProduto);
    }
}
