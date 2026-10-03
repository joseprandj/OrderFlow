package com.github.joseprandj.OrderFlow_Logistica.exception;

import java.util.UUID;

public class PedidoJaPossuiLogisticaException extends RuntimeException {

    public PedidoJaPossuiLogisticaException(UUID idPedido) {
        super("O pedido " + idPedido + " já possui um registro de logística associado.");
    }
}
