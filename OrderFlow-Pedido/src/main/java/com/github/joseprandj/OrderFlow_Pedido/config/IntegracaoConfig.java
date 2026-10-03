package com.github.joseprandj.OrderFlow_Pedido.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class IntegracaoConfig {

    public static final String CLIENTE_REST_CLIENT = "clienteRestClient";
    public static final String PRODUTO_REST_CLIENT = "produtoRestClient";
    public static final String ESTOQUE_REST_CLIENT = "estoqueRestClient";

    @Bean(CLIENTE_REST_CLIENT)
    RestClient clienteRestClient(@Value("${orderflow.integracao.cliente-url}") String url) {
        return RestClient.builder().baseUrl(url).build();
    }

    @Bean(PRODUTO_REST_CLIENT)
    RestClient produtoRestClient(@Value("${orderflow.integracao.produto-url}") String url) {
        return RestClient.builder().baseUrl(url).build();
    }

    @Bean(ESTOQUE_REST_CLIENT)
    RestClient estoqueRestClient(@Value("${orderflow.integracao.estoque-url}") String url) {
        return RestClient.builder().baseUrl(url).build();
    }
}
