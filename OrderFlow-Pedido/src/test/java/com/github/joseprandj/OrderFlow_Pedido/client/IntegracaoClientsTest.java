package com.github.joseprandj.OrderFlow_Pedido.client;

import com.github.joseprandj.OrderFlow_Pedido.exception.ClienteNaoEncontradoException;
import com.github.joseprandj.OrderFlow_Pedido.exception.EstoqueNaoEncontradoException;
import com.github.joseprandj.OrderFlow_Pedido.exception.IntegracaoException;
import com.github.joseprandj.OrderFlow_Pedido.exception.ProdutoNaoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class IntegracaoClientsTest {

    private MockRestServiceServer servidor;
    private RestClient restClient;

    @BeforeEach
    void configurar() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://servico");
        servidor = MockRestServiceServer.bindTo(builder).build();
        restClient = builder.build();
    }

    @Test
    void buscarClientePorCpfCnpjDeveRetornarSeuIdentificador() {
        UUID id = UUID.randomUUID();
        servidor.expect(requestTo("http://servico/clientes/12345678901"))
                .andRespond(withSuccess("""
                        {"id":"%s","cpfCnpj":"12345678901","nome":"Maria","tipo":"F"}
                        """.formatted(id), MediaType.APPLICATION_JSON));

        assertThat(new ClienteClient(restClient).buscarIdPorCpfCnpj("12345678901")).isEqualTo(id);
    }

    @Test
    void clienteInexistenteDeveSerConvertidoEmExcecaoDeNegocio() {
        servidor.expect(requestTo("http://servico/clientes/12345678901")).andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThatThrownBy(() -> new ClienteClient(restClient).buscarIdPorCpfCnpj("12345678901"))
                .isInstanceOf(ClienteNaoEncontradoException.class);
    }

    @Test
    void falhaNoServicoDeClienteDeveSerConvertidaEmExcecaoDeIntegracao() {
        servidor.expect(requestTo("http://servico/clientes/12345678901")).andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThatThrownBy(() -> new ClienteClient(restClient).buscarIdPorCpfCnpj("12345678901"))
                .isInstanceOf(IntegracaoException.class);
    }

    @Test
    void buscarProdutoDeveLerSkuPrecoEAtivoIgnorandoDemaisCampos() {
        UUID id = UUID.randomUUID();
        servidor.expect(requestTo("http://servico/produtos/" + id))
                .andRespond(withSuccess("""
                        {"id":"%s","sku":"CANETA","nome":"Caneta","preco":2.50,"ativo":true}
                        """.formatted(id), MediaType.APPLICATION_JSON));

        ProdutoDados produto = new ProdutoClient(restClient).buscar(id);

        assertThat(produto.sku()).isEqualTo("CANETA");
        assertThat(produto.preco()).isEqualByComparingTo("2.50");
        assertThat(produto.ativo()).isTrue();
    }

    @Test
    void produtoInexistenteDeveSerConvertidoEmExcecaoDeNegocio() {
        UUID id = UUID.randomUUID();
        servidor.expect(requestTo("http://servico/produtos/" + id)).andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThatThrownBy(() -> new ProdutoClient(restClient).buscar(id))
                .isInstanceOf(ProdutoNaoEncontradoException.class);
    }

    @Test
    void consultarEAtualizarQuantidadeDoEstoque() {
        servidor.expect(requestTo("http://servico/estoques/CANETA"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"sku\":\"CANETA\",\"quantidade\":7}", MediaType.APPLICATION_JSON));
        servidor.expect(requestTo("http://servico/estoques/CANETA"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(content().json("{\"quantidade\":3}"))
                .andRespond(withSuccess());

        EstoqueClient estoqueClient = new EstoqueClient(restClient);

        assertThat(estoqueClient.consultarQuantidade("CANETA")).isEqualTo(7);
        estoqueClient.atualizarQuantidade("CANETA", 3);
        servidor.verify();
    }

    @Test
    void estoqueInexistenteDeveSerConvertidoEmExcecaoDeNegocio() {
        servidor.expect(requestTo("http://servico/estoques/CANETA")).andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThatThrownBy(() -> new EstoqueClient(restClient).consultarQuantidade("CANETA"))
                .isInstanceOf(EstoqueNaoEncontradoException.class);
    }
}
