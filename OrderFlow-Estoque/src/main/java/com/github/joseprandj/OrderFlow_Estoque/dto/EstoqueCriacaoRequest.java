package com.github.joseprandj.OrderFlow_Estoque.dto;

import jakarta.validation.constraints.NotBlank;

import static org.springframework.util.StringUtils.trimWhitespace;

public record EstoqueCriacaoRequest(
        @NotBlank(message = "é obrigatório")
        String sku
) {

    public EstoqueCriacaoRequest {
        sku = trimWhitespace(sku);
    }
}
