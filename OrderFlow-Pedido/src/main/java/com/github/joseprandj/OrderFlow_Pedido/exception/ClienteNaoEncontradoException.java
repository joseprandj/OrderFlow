package com.github.joseprandj.OrderFlow_Pedido.exception;

public class ClienteNaoEncontradoException extends RuntimeException {

    public ClienteNaoEncontradoException(String cpfCnpj) {
        super("Cliente não encontrado para o CPF/CNPJ " + cpfCnpj + ".");
    }
}
