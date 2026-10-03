package com.github.joseprandj.OrderFlow_Produto.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class IntegracaoConfig {

    @Bean
    RestClient estoqueRestClient(@Value("${orderflow.integracao.estoque-url}") String estoqueUrl) {
        return RestClient.builder().baseUrl(estoqueUrl).build();
    }
}
