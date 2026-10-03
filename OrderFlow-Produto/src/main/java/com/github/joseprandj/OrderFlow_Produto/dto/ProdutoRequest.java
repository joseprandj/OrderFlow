package com.github.joseprandj.OrderFlow_Produto.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

import static org.springframework.util.StringUtils.trimWhitespace;

public record ProdutoRequest(
        @NotBlank(message = "é obrigatório")
        String sku,

        @NotBlank(message = "é obrigatório")
        String nome,

        String descricao,

        @NotNull(message = "é obrigatório")
        @Positive(message = "deve ser maior que zero")
        @Digits(integer = 17, fraction = 2, message = "deve possuir no máximo 2 casas decimais")
        BigDecimal preco,

        Boolean ativo
) {

    public ProdutoRequest {
        sku = trimWhitespace(sku);
        nome = trimWhitespace(nome);
        descricao = trimWhitespace(descricao);
    }
}
