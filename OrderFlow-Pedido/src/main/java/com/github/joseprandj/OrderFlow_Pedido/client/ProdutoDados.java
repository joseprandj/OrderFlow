package com.github.joseprandj.OrderFlow_Pedido.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Dados do produto utilizados pelo domínio Pedido.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProdutoDados(UUID id, String sku, BigDecimal preco, boolean ativo) {
}
