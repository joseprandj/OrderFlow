package com.github.joseprandj.OrderFlow_Cliente.exception;

public class ClienteNaoEncontradoException extends RuntimeException {

    public ClienteNaoEncontradoException(String identificador) {
        super("Cliente não encontrado: " + identificador);
    }
}
