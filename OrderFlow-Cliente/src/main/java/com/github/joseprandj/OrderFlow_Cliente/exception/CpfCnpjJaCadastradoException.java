package com.github.joseprandj.OrderFlow_Cliente.exception;

public class CpfCnpjJaCadastradoException extends RuntimeException {

    public CpfCnpjJaCadastradoException(String cpfCnpj) {
        super("Já existe um cliente cadastrado com o CPF/CNPJ " + cpfCnpj + ".");
    }
}
