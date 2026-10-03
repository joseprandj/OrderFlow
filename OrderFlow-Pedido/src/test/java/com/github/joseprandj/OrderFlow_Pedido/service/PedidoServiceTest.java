package com.github.joseprandj.OrderFlow_Pedido.service;

import com.github.joseprandj.OrderFlow_Pedido.client.ClienteClient;
import com.github.joseprandj.OrderFlow_Pedido.client.ProdutoClient;
import com.github.joseprandj.OrderFlow_Pedido.client.ProdutoDados;
import com.github.joseprandj.OrderFlow_Pedido.dto.ItemPedidoQuantidadeRequest;
import com.github.joseprandj.OrderFlow_Pedido.dto.ItemPedidoRequest;
import com.github.joseprandj.OrderFlow_Pedido.dto.PedidoRequest;
import com.github.joseprandj.OrderFlow_Pedido.dto.PedidoResponse;
import com.github.joseprandj.OrderFlow_Pedido.exception.ClienteNaoEncontradoException;
import com.github.joseprandj.OrderFlow_Pedido.exception.EstoqueInsuficienteException;
import com.github.joseprandj.OrderFlow_Pedido.exception.ItemDuplicadoException;
import com.github.joseprandj.OrderFlow_Pedido.exception.ItemPedidoNaoEncontradoException;
import com.github.joseprandj.OrderFlow_Pedido.exception.PedidoNaoEncontradoException;
import com.github.joseprandj.OrderFlow_Pedido.exception.ProdutoInativoException;
import com.github.joseprandj.OrderFlow_Pedido.exception.ProdutoNaoEncontradoException;
import com.github.joseprandj.OrderFlow_Pedido.model.ItemPedido;
import com.github.joseprandj.OrderFlow_Pedido.model.Pedido;
import com.github.joseprandj.OrderFlow_Pedido.repository.PedidoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    private static final String CPF = "12345678901";
    private static final ProdutoDados CANETA = new ProdutoDados(UUID.randomUUID(), "CANETA", new BigDecimal("2.50"), true);
    private static final ProdutoDados CADERNO = new ProdutoDados(UUID.randomUUID(), "CADERNO", new BigDecimal("15.00"), true);
    private static final ProdutoDados LAPIS_INATIVO = new ProdutoDados(UUID.randomUUID(), "LAPIS", new BigDecimal("1.00"), false);

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ClienteClient clienteClient;

    @Mock
    private ProdutoClient produtoClient;

    @Mock
    private AjusteEstoqueService ajusteEstoqueService;

    @InjectMocks
    private PedidoService pedidoService;

    @Test
    void criarDeveCalcularValoresUsandoPrecoDoProdutoEDebitarEstoque() {
        UUID idCliente = UUID.randomUUID();
        when(clienteClient.buscarIdPorCpfCnpj(CPF)).thenReturn(idCliente);
        when(produtoClient.buscar(CANETA.id())).thenReturn(CANETA);
        when(produtoClient.buscar(CADERNO.id())).thenReturn(CADERNO);
        when(pedidoRepository.saveAndFlush(any(Pedido.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        PedidoResponse response = pedidoService.criar(new PedidoRequest(CPF, List.of(
                new ItemPedidoRequest(CANETA.id(), 4),
                new ItemPedidoRequest(CADERNO.id(), 2))));

        assertThat(response.idCliente()).isEqualTo(idCliente);
        assertThat(response.itens()).hasSize(2);
        assertThat(response.itens().getFirst().preco()).isEqualByComparingTo("2.50");
        assertThat(response.itens().getFirst().valorTotal()).isEqualByComparingTo("10.00");
        assertThat(response.valorTotal()).isEqualByComparingTo("40.00");
        assertThat(response.idFinanceiro()).isNull();
        assertThat(response.idLogistica()).isNull();

        var ordem = inOrder(clienteClient, pedidoRepository, ajusteEstoqueService);
        ordem.verify(clienteClient).buscarIdPorCpfCnpj(CPF);
        ordem.verify(pedidoRepository).saveAndFlush(any(Pedido.class));
        ordem.verify(ajusteEstoqueService).aplicar(List.of(
                AjusteEstoque.debito("CANETA", 4),
                AjusteEstoque.debito("CADERNO", 2)));
    }

    @Test
    void criarDeveRejeitarClienteInexistente() {
        when(clienteClient.buscarIdPorCpfCnpj(CPF)).thenThrow(new ClienteNaoEncontradoException(CPF));

        assertThatThrownBy(() -> pedidoService.criar(
                new PedidoRequest(CPF, List.of(new ItemPedidoRequest(CANETA.id(), 1)))))
                .isInstanceOf(ClienteNaoEncontradoException.class);
        verifyNoInteractions(pedidoRepository, ajusteEstoqueService);
    }

    @Test
    void criarDeveRejeitarProdutoInexistente() {
        UUID idProduto = UUID.randomUUID();
        when(produtoClient.buscar(idProduto)).thenThrow(new ProdutoNaoEncontradoException(idProduto));

        assertThatThrownBy(() -> pedidoService.criar(
                new PedidoRequest(CPF, List.of(new ItemPedidoRequest(idProduto, 1)))))
                .isInstanceOf(ProdutoNaoEncontradoException.class);
        verifyNoInteractions(pedidoRepository, ajusteEstoqueService);
    }

    @Test
    void criarDeveRejeitarProdutoInativo() {
        when(produtoClient.buscar(LAPIS_INATIVO.id())).thenReturn(LAPIS_INATIVO);

        assertThatThrownBy(() -> pedidoService.criar(
                new PedidoRequest(CPF, List.of(new ItemPedidoRequest(LAPIS_INATIVO.id(), 1)))))
                .isInstanceOf(ProdutoInativoException.class);
        verifyNoInteractions(pedidoRepository, ajusteEstoqueService);
    }

    @Test
    void incluirItemDeveRejeitarProdutoInativo() {
        Pedido pedido = pedidoComItem(CANETA, 2);
        when(pedidoRepository.findById(pedido.getId())).thenReturn(Optional.of(pedido));
        when(produtoClient.buscar(LAPIS_INATIVO.id())).thenReturn(LAPIS_INATIVO);

        assertThatThrownBy(() -> pedidoService.incluirItem(pedido.getId(), new ItemPedidoRequest(LAPIS_INATIVO.id(), 1)))
                .isInstanceOf(ProdutoInativoException.class);
        verify(pedidoRepository, never()).saveAndFlush(any());
        verifyNoInteractions(ajusteEstoqueService);
    }

    @Test
    void criarDeveRejeitarItensComMesmoSku() {
        when(produtoClient.buscar(CANETA.id())).thenReturn(CANETA);

        assertThatThrownBy(() -> pedidoService.criar(new PedidoRequest(CPF, List.of(
                new ItemPedidoRequest(CANETA.id(), 1),
                new ItemPedidoRequest(CANETA.id(), 3)))))
                .isInstanceOf(ItemDuplicadoException.class);
        verifyNoInteractions(pedidoRepository, ajusteEstoqueService);
    }

    @Test
    void criarDevePropagarEstoqueInsuficiente() {
        when(produtoClient.buscar(CANETA.id())).thenReturn(CANETA);
        when(pedidoRepository.saveAndFlush(any(Pedido.class))).thenAnswer(invocacao -> invocacao.getArgument(0));
        doThrow(new EstoqueInsuficienteException("CANETA", 1, 5)).when(ajusteEstoqueService).aplicar(any());

        assertThatThrownBy(() -> pedidoService.criar(
                new PedidoRequest(CPF, List.of(new ItemPedidoRequest(CANETA.id(), 5)))))
                .isInstanceOf(EstoqueInsuficienteException.class);
    }

    @Test
    void incluirItemDeveRecalcularTotalEDebitarEstoque() {
        Pedido pedido = pedidoComItem(CANETA, 2);
        when(pedidoRepository.findById(pedido.getId())).thenReturn(Optional.of(pedido));
        when(produtoClient.buscar(CADERNO.id())).thenReturn(CADERNO);
        when(pedidoRepository.saveAndFlush(pedido)).thenReturn(pedido);

        PedidoResponse response = pedidoService.incluirItem(pedido.getId(), new ItemPedidoRequest(CADERNO.id(), 1));

        assertThat(response.itens()).hasSize(2);
        assertThat(response.valorTotal()).isEqualByComparingTo("20.00");
        verify(ajusteEstoqueService).aplicar(List.of(AjusteEstoque.debito("CADERNO", 1)));
    }

    @Test
    void incluirItemDeveRejeitarSkuJaExistenteNoPedido() {
        Pedido pedido = pedidoComItem(CANETA, 2);
        when(pedidoRepository.findById(pedido.getId())).thenReturn(Optional.of(pedido));
        when(produtoClient.buscar(CANETA.id())).thenReturn(CANETA);

        assertThatThrownBy(() -> pedidoService.incluirItem(pedido.getId(), new ItemPedidoRequest(CANETA.id(), 1)))
                .isInstanceOf(ItemDuplicadoException.class);
        verify(pedidoRepository, never()).saveAndFlush(any());
        verifyNoInteractions(ajusteEstoqueService);
    }

    @Test
    void alterarItemDeveManterPrecoDaInclusaoEAjustarEstoquePelaDiferenca() {
        Pedido pedido = pedidoComItem(CANETA, 2);
        ItemPedido item = pedido.getItens().getFirst();
        when(pedidoRepository.findById(pedido.getId())).thenReturn(Optional.of(pedido));
        when(pedidoRepository.saveAndFlush(pedido)).thenReturn(pedido);

        PedidoResponse response = pedidoService.alterarItem(pedido.getId(), item.getId(), new ItemPedidoQuantidadeRequest(5));

        assertThat(response.itens().getFirst().quantidade()).isEqualTo(5);
        assertThat(response.itens().getFirst().preco()).isEqualByComparingTo("2.50");
        assertThat(response.valorTotal()).isEqualByComparingTo("12.50");
        verify(ajusteEstoqueService).aplicar(List.of(AjusteEstoque.debito("CANETA", 3)));
        verifyNoInteractions(produtoClient);
    }

    @Test
    void alterarItemComQuantidadeMenorDeveDevolverEstoque() {
        Pedido pedido = pedidoComItem(CANETA, 5);
        ItemPedido item = pedido.getItens().getFirst();
        when(pedidoRepository.findById(pedido.getId())).thenReturn(Optional.of(pedido));
        when(pedidoRepository.saveAndFlush(pedido)).thenReturn(pedido);

        pedidoService.alterarItem(pedido.getId(), item.getId(), new ItemPedidoQuantidadeRequest(1));

        verify(ajusteEstoqueService).aplicar(List.of(AjusteEstoque.devolucao("CANETA", 4)));
    }

    @Test
    void alterarItemDeveLancarExcecaoQuandoItemNaoPertenceAoPedido() {
        Pedido pedido = pedidoComItem(CANETA, 1);
        when(pedidoRepository.findById(pedido.getId())).thenReturn(Optional.of(pedido));

        assertThatThrownBy(() -> pedidoService.alterarItem(pedido.getId(), UUID.randomUUID(),
                new ItemPedidoQuantidadeRequest(2)))
                .isInstanceOf(ItemPedidoNaoEncontradoException.class);
    }

    @Test
    void removerItemDeveRecalcularTotalEDevolverEstoque() {
        Pedido pedido = pedidoComItem(CANETA, 2);
        ItemPedido item = pedido.getItens().getFirst();
        when(pedidoRepository.findById(pedido.getId())).thenReturn(Optional.of(pedido));
        when(pedidoRepository.saveAndFlush(pedido)).thenReturn(pedido);

        PedidoResponse response = pedidoService.removerItem(pedido.getId(), item.getId());

        assertThat(response.itens()).isEmpty();
        assertThat(response.valorTotal()).isEqualByComparingTo("0");
        verify(ajusteEstoqueService).aplicar(List.of(AjusteEstoque.devolucao("CANETA", 2)));
    }

    @Test
    void excluirDeveRemoverPedidoEDevolverEstoqueDeTodosOsItens() {
        Pedido pedido = pedidoComItem(CANETA, 2);
        ItemPedido caderno = new ItemPedido(CADERNO.id(), CADERNO.sku(), CADERNO.preco(), 3);
        caderno.setId(UUID.randomUUID());
        pedido.adicionarItem(caderno);
        when(pedidoRepository.findById(pedido.getId())).thenReturn(Optional.of(pedido));

        pedidoService.excluir(pedido.getId());

        var ordem = inOrder(pedidoRepository, ajusteEstoqueService);
        ordem.verify(pedidoRepository).delete(pedido);
        ordem.verify(pedidoRepository).flush();
        ordem.verify(ajusteEstoqueService).aplicar(List.of(
                AjusteEstoque.devolucao("CANETA", 2),
                AjusteEstoque.devolucao("CADERNO", 3)));
    }

    @Test
    void buscarPorIdDeveLancarExcecaoQuandoNaoEncontrado() {
        UUID id = UUID.randomUUID();
        when(pedidoRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pedidoService.buscarPorId(id))
                .isInstanceOf(PedidoNaoEncontradoException.class);
    }

    private static Pedido pedidoComItem(ProdutoDados produto, int quantidade) {
        Pedido pedido = new Pedido();
        pedido.setId(UUID.randomUUID());
        pedido.setIdCliente(UUID.randomUUID());
        ItemPedido item = new ItemPedido(produto.id(), produto.sku(), produto.preco(), quantidade);
        item.setId(UUID.randomUUID());
        pedido.adicionarItem(item);
        return pedido;
    }
}
