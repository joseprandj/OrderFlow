package com.github.joseprandj.OrderFlow_Cliente.dto;

import com.github.joseprandj.OrderFlow_Cliente.model.Cliente;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import static org.springframework.util.StringUtils.trimWhitespace;

/**
 * Campos nulos não são alterados. Os valores são normalizados (trim) antes da validação,
 * por isso {@code @Size(min = 1)} impede valores vazios ou compostos apenas por espaços.
 */
public record ClienteAtualizacaoParcialRequest(
        @Pattern(regexp = Cliente.FORMATO_CPF_CNPJ, message = Cliente.MENSAGEM_CPF_CNPJ_INVALIDO)
        String cpfCnpj,

        @Size(min = 1, message = "não pode ser vazio")
        String nome,

        @Size(min = 1, message = "não pode ser vazio")
        String telefone,

        @Size(min = 1, message = "não pode ser vazio")
        @Email(message = "deve possuir um formato de e-mail válido")
        String email,

        String endereco
) {

    public ClienteAtualizacaoParcialRequest {
        cpfCnpj = trimWhitespace(cpfCnpj);
        nome = trimWhitespace(nome);
        telefone = trimWhitespace(telefone);
        email = trimWhitespace(email);
        endereco = trimWhitespace(endereco);
    }
}
