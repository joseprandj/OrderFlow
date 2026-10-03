package com.github.joseprandj.OrderFlow_Pedido.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.joseprandj.OrderFlow_Pedido.config.IntegracaoConfig;
import com.github.joseprandj.OrderFlow_Pedido.exception.ClienteNaoEncontradoException;
import com.github.joseprandj.OrderFlow_Pedido.exception.IntegracaoException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

/**
 * Cliente HTTP do domínio Cliente.
 */
@Component
public class ClienteClient {

    private static final String DOMINIO = "Cliente";

    private final RestClient restClient;

    public ClienteClient(@Qualifier(IntegracaoConfig.CLIENTE_REST_CLIENT) RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Consulta o cliente pelo {@code cpfCnpj} e retorna o seu identificador.
     *
     * @throws ClienteNaoEncontradoException quando não existe cliente com o {@code cpfCnpj} informado
     */
    public UUID buscarIdPorCpfCnpj(String cpfCnpj) {
        try {
            ClienteDados cliente = restClient.get()
                    .uri("/clientes/{cpfCnpj}", cpfCnpj)
                    .retrieve()
                    .onStatus(status -> status.isSameCodeAs(HttpStatus.NOT_FOUND), (requisicao, resposta) -> {
                        throw new ClienteNaoEncontradoException(cpfCnpj);
                    })
                    .body(ClienteDados.class);
            if (cliente == null || cliente.id() == null) {
                throw new IntegracaoException(DOMINIO, null);
            }
            return cliente.id();
        } catch (RestClientException e) {
            throw new IntegracaoException(DOMINIO, e);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record ClienteDados(UUID id) {
    }
}
