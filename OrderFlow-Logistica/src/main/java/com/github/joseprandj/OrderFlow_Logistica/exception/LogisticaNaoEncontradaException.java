package com.github.joseprandj.OrderFlow_Logistica.exception;

import java.util.UUID;

public class LogisticaNaoEncontradaException extends RuntimeException {

    public LogisticaNaoEncontradaException(UUID id) {
        super("Registro de logística não encontrado: " + id);
    }
}
