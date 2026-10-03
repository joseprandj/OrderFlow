package com.github.joseprandj.OrderFlow_Pedido.service;

import com.github.joseprandj.OrderFlow_Pedido.client.ClienteClient;
import com.github.joseprandj.OrderFlow_Pedido.client.ProdutoClient;
import com.github.joseprandj.OrderFlow_Pedido.client.ProdutoDados;
import com.github.joseprandj.OrderFlow_Pedido.dto.ItemPedidoQuantidadeRequest;
import com.github.joseprandj.OrderFlow_Pedido.dto.ItemPedidoRequest;
import com.github.joseprandj.OrderFlow_Pedido.dto.PedidoRequest;
import com.github.joseprandj.OrderFlow_Pedido.dto.PedidoResponse;
import com.github.joseprandj.OrderFlow_Pedido.exception.ItemDuplicadoException;
import com.github.joseprandj.OrderFlow_Pedido.exception.ItemPedidoNaoEncontradoException;
import com.github.joseprandj.OrderFlow_Pedido.exception.PedidoNaoEncontradoException;
import com.github.joseprandj.OrderFlow_Pedido.exception.ProdutoInativoException;
import com.github.joseprandj.OrderFlow_Pedido.model.ItemPedido;
import com.github.joseprandj.OrderFlow_Pedido.model.Pedido;
import com.github.joseprandj.OrderFlow_Pedido.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Regras do pedido. As alterações de estoque são aplicadas após a persistência do pedido (flush):
 * se o ajuste de estoque falhar, a exceção reverte a transação do pedido.
 */
@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteClient clienteClient;
    private final ProdutoClient produtoClient;
    private final AjusteEstoqueService ajusteEstoqueService;

    @Transactional
    public PedidoResponse criar(PedidoRequest request) {
        UUID idCliente = clienteClient.buscarIdPorCpfCnpj(request.cpfCnpj());

        Pedido pedido = new Pedido();
        pedido.setIdCliente(idCliente);
        for (ItemPedidoRequest itemRequest : request.itens()) {
            adicionarItem(pedido, itemRequest);
        }

        Pedido salvo = pedidoRepository.saveAndFlush(pedido);
        ajusteEstoqueService.aplicar(salvo.getItens().stream()
                .map(item -> AjusteEstoque.debito(item.getSku(), item.getQuantidade()))
                .toList());
        return PedidoResponse.from(salvo);
    }

    @Transactional(readOnly = true)
    public Page<PedidoResponse> listar(Pageable pageable) {
        return pedidoRepository.findAll(pageable).map(PedidoResponse::from);
    }

    @Transactional(readOnly = true)
    public PedidoResponse buscarPorId(UUID id) {
        return PedidoResponse.from(obterPedido(id));
    }

    @Transactional
    public void excluir(UUID id) {
        Pedido pedido = obterPedido(id);
        List<AjusteEstoque> devolucoes = pedido.getItens().stream()
                .map(item -> AjusteEstoque.devolucao(item.getSku(), item.getQuantidade()))
                .toList();

        pedidoRepository.delete(pedido);
        pedidoRepository.flush();
        ajusteEstoqueService.aplicar(devolucoes);
    }

    @Transactional
    public PedidoResponse incluirItem(UUID idPedido, ItemPedidoRequest request) {
        Pedido pedido = obterPedido(idPedido);
        ItemPedido item = adicionarItem(pedido, request);

        Pedido salvo = pedidoRepository.saveAndFlush(pedido);
        ajusteEstoqueService.aplicar(List.of(AjusteEstoque.debito(item.getSku(), item.getQuantidade())));
        return PedidoResponse.from(salvo);
    }

    @Transactional
    public PedidoResponse alterarItem(UUID idPedido, UUID idItem, ItemPedidoQuantidadeRequest request) {
        Pedido pedido = obterPedido(idPedido);
        ItemPedido item = obterItem(pedido, idItem);
        int diferenca = request.quantidade() - item.getQuantidade();

        item.definirQuantidade(request.quantidade());
        pedido.recalcularValorTotal();

        Pedido salvo = pedidoRepository.saveAndFlush(pedido);
        ajusteEstoqueService.aplicar(List.of(AjusteEstoque.debito(item.getSku(), diferenca)));
        return PedidoResponse.from(salvo);
    }

    @Transactional
    public PedidoResponse removerItem(UUID idPedido, UUID idItem) {
        Pedido pedido = obterPedido(idPedido);
        ItemPedido item = obterItem(pedido, idItem);

        pedido.removerItem(item);

        Pedido salvo = pedidoRepository.saveAndFlush(pedido);
        ajusteEstoqueService.aplicar(List.of(AjusteEstoque.devolucao(item.getSku(), item.getQuantidade())));
        return PedidoResponse.from(salvo);
    }

    private ItemPedido adicionarItem(Pedido pedido, ItemPedidoRequest request) {
        ProdutoDados produto = produtoClient.buscar(request.idProduto());
        if (!produto.ativo()) {
            throw new ProdutoInativoException(produto.id());
        }
        if (pedido.possuiItemComSku(produto.sku())) {
            throw new ItemDuplicadoException(produto.sku());
        }
        ItemPedido item = new ItemPedido(produto.id(), produto.sku(), produto.preco(), request.quantidade());
        pedido.adicionarItem(item);
        return item;
    }

    private Pedido obterPedido(UUID id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new PedidoNaoEncontradoException(id));
    }

    private ItemPedido obterItem(Pedido pedido, UUID idItem) {
        return pedido.buscarItem(idItem)
                .orElseThrow(() -> new ItemPedidoNaoEncontradoException(idItem));
    }
}
