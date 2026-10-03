package com.github.joseprandj.OrderFlow_Financeiro.exception;

import java.util.UUID;

public class PedidoJaPossuiFinanceiroException extends RuntimeException {

    public PedidoJaPossuiFinanceiroException(UUID idPedido) {
        super("O pedido " + idPedido + " já possui um financeiro associado.");
    }
}
