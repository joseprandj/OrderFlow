package com.github.joseprandj.OrderFlow_Cliente.dto;

import com.github.joseprandj.OrderFlow_Cliente.model.Cliente;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import static org.springframework.util.StringUtils.trimWhitespace;

public record ClienteRequest(
        @NotBlank(message = "é obrigatório")
        @Pattern(regexp = Cliente.FORMATO_CPF_CNPJ, message = Cliente.MENSAGEM_CPF_CNPJ_INVALIDO)
        String cpfCnpj,

        @NotBlank(message = "é obrigatório")
        String nome,

        @NotBlank(message = "é obrigatório")
        String telefone,

        @NotBlank(message = "é obrigatório")
        @Email(message = "deve possuir um formato de e-mail válido")
        String email,

        String endereco
) {

    public ClienteRequest {
        cpfCnpj = trimWhitespace(cpfCnpj);
        nome = trimWhitespace(nome);
        telefone = trimWhitespace(telefone);
        email = trimWhitespace(email);
        endereco = trimWhitespace(endereco);
    }
}
