package com.github.joseprandj.OrderFlow_Produto.client;

import com.github.joseprandj.OrderFlow_Produto.exception.IntegracaoEstoqueException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Cliente HTTP do domínio Estoque.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EstoqueClient {

    private final RestClient estoqueRestClient;

    /**
     * Cria o estoque inicial (quantidade 0) do SKU. Caso o Estoque já possua registro para o SKU
     * (ex.: produto excluído e recriado), o registro existente é mantido.
     */
    public void criarEstoque(String sku) {
        try {
            estoqueRestClient.post()
                    .uri("/estoques")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new CriacaoEstoqueRequest(sku))
                    .retrieve()
                    .onStatus(status -> status.isSameCodeAs(HttpStatus.CONFLICT),
                            (requisicao, resposta) -> log.info("Estoque já existente para o SKU {}.", sku))
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.error("Falha ao criar o estoque do SKU {}.", sku, e);
            throw new IntegracaoEstoqueException(e);
        }
    }

    record CriacaoEstoqueRequest(String sku) {
    }
}
