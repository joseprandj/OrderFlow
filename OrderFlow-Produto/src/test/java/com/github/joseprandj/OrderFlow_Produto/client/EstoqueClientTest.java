package com.github.joseprandj.OrderFlow_Produto.client;

import com.github.joseprandj.OrderFlow_Produto.exception.IntegracaoEstoqueException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class EstoqueClientTest {

    private MockRestServiceServer servidor;
    private EstoqueClient estoqueClient;

    @BeforeEach
    void configurar() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://estoque");
        servidor = MockRestServiceServer.bindTo(builder).build();
        estoqueClient = new EstoqueClient(builder.build());
    }

    @Test
    void criarEstoqueDeveEnviarSku() {
        servidor.expect(requestTo("http://estoque/estoques"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"sku\":\"SKU-1\"}"))
                .andRespond(withSuccess());

        estoqueClient.criarEstoque("SKU-1");

        servidor.verify();
    }

    @Test
    void criarEstoqueDeveManterEstoqueExistenteQuandoSkuJaPossuiRegistro() {
        servidor.expect(requestTo("http://estoque/estoques")).andRespond(withStatus(HttpStatus.CONFLICT));

        assertThatCode(() -> estoqueClient.criarEstoque("SKU-1")).doesNotThrowAnyException();
    }

    @Test
    void criarEstoqueDeveLancarExcecaoDeIntegracaoQuandoEstoqueFalha() {
        servidor.expect(requestTo("http://estoque/estoques")).andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));

        assertThatThrownBy(() -> estoqueClient.criarEstoque("SKU-1"))
                .isInstanceOf(IntegracaoEstoqueException.class);
    }
}
