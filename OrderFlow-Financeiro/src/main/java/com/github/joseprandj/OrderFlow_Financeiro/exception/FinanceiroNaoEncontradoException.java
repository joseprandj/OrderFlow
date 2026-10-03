package com.github.joseprandj.OrderFlow_Financeiro.exception;

import java.util.UUID;

public class FinanceiroNaoEncontradoException extends RuntimeException {

    public FinanceiroNaoEncontradoException(UUID id) {
        super("Financeiro não encontrado: " + id);
    }
}
