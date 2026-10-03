package com.github.joseprandj.OrderFlow_Pedido.client;

import com.github.joseprandj.OrderFlow_Pedido.config.IntegracaoConfig;
import com.github.joseprandj.OrderFlow_Pedido.exception.IntegracaoException;
import com.github.joseprandj.OrderFlow_Pedido.exception.ProdutoNaoEncontradoException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

/**
 * Cliente HTTP do domínio Produto.
 */
@Component
public class ProdutoClient {

    private static final String DOMINIO = "Produto";

    private final RestClient restClient;

    public ProdutoClient(@Qualifier(IntegracaoConfig.PRODUTO_REST_CLIENT) RestClient restClient) {
        this.restClient = restClient;
    }

    public ProdutoDados buscar(UUID idProduto) {
        try {
            return restClient.get()
                    .uri("/produtos/{id}", idProduto)
                    .retrieve()
                    .onStatus(status -> status.isSameCodeAs(HttpStatus.NOT_FOUND), (requisicao, resposta) -> {
                        throw new ProdutoNaoEncontradoException(idProduto);
                    })
                    .body(ProdutoDados.class);
        } catch (RestClientException e) {
            throw new IntegracaoException(DOMINIO, e);
        }
    }
}
