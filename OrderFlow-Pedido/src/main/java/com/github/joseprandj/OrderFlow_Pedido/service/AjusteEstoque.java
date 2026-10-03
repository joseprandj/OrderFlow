package com.github.joseprandj.OrderFlow_Pedido.service;

/**
 * Variação de estoque de um SKU. {@code quantidadeDebitada} positiva retira unidades do estoque
 * e negativa devolve unidades ao estoque.
 */
public record AjusteEstoque(String sku, int quantidadeDebitada) {

    public static AjusteEstoque debito(String sku, int quantidade) {
        return new AjusteEstoque(sku, quantidade);
    }

    public static AjusteEstoque devolucao(String sku, int quantidade) {
        return new AjusteEstoque(sku, -quantidade);
    }

    public AjusteEstoque inverso() {
        return new AjusteEstoque(sku, -quantidadeDebitada);
    }
}
