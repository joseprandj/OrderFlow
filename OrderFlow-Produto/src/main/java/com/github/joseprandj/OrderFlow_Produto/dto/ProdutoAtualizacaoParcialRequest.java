package com.github.joseprandj.OrderFlow_Produto.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

import static org.springframework.util.StringUtils.trimWhitespace;

/**
 * Atualização parcial: campos nulos não são alterados. {@code id} e {@code sku} não podem ser alterados.
 * Os valores são normalizados (trim) antes da validação, por isso {@code @Size(min = 1)} impede nome vazio.
 */
public record ProdutoAtualizacaoParcialRequest(
        @Size(min = 1, message = "não pode ser vazio")
        String nome,

        String descricao,

        @Positive(message = "deve ser maior que zero")
        @Digits(integer = 17, fraction = 2, message = "deve possuir no máximo 2 casas decimais")
        BigDecimal preco,

        Boolean ativo
) {

    public ProdutoAtualizacaoParcialRequest {
        nome = trimWhitespace(nome);
        descricao = trimWhitespace(descricao);
    }
}
