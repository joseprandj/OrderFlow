package com.github.joseprandj.OrderFlow_Pedido.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.joseprandj.OrderFlow_Pedido.config.IntegracaoConfig;
import com.github.joseprandj.OrderFlow_Pedido.exception.EstoqueNaoEncontradoException;
import com.github.joseprandj.OrderFlow_Pedido.exception.IntegracaoException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Cliente HTTP do domínio Estoque.
 */
@Component
public class EstoqueClient {

    private static final String DOMINIO = "Estoque";

    private final RestClient restClient;

    public EstoqueClient(@Qualifier(IntegracaoConfig.ESTOQUE_REST_CLIENT) RestClient restClient) {
        this.restClient = restClient;
    }

    public int consultarQuantidade(String sku) {
        try {
            EstoqueDados estoque = restClient.get()
                    .uri("/estoques/{sku}", sku)
                    .retrieve()
                    .onStatus(status -> status.isSameCodeAs(HttpStatus.NOT_FOUND), (requisicao, resposta) -> {
                        throw new EstoqueNaoEncontradoException(sku);
                    })
                    .body(EstoqueDados.class);
            if (estoque == null) {
                throw new IntegracaoException(DOMINIO, null);
            }
            return estoque.quantidade();
        } catch (RestClientException e) {
            throw new IntegracaoException(DOMINIO, e);
        }
    }

    public void atualizarQuantidade(String sku, int quantidade) {
        try {
            restClient.patch()
                    .uri("/estoques/{sku}", sku)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new AtualizacaoQuantidadeRequest(quantidade))
                    .retrieve()
                    .onStatus(status -> status.isSameCodeAs(HttpStatus.NOT_FOUND), (requisicao, resposta) -> {
                        throw new EstoqueNaoEncontradoException(sku);
                    })
                    .toBodilessEntity();
        } catch (RestClientException e) {
            throw new IntegracaoException(DOMINIO, e);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record EstoqueDados(String sku, int quantidade) {
    }

    record AtualizacaoQuantidadeRequest(int quantidade) {
    }
}
