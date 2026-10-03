package com.github.joseprandj.OrderFlow_Produto.service;

import com.github.joseprandj.OrderFlow_Produto.client.EstoqueClient;
import com.github.joseprandj.OrderFlow_Produto.dto.ProdutoAtualizacaoParcialRequest;
import com.github.joseprandj.OrderFlow_Produto.dto.ProdutoAtualizacaoRequest;
import com.github.joseprandj.OrderFlow_Produto.dto.ProdutoRequest;
import com.github.joseprandj.OrderFlow_Produto.dto.ProdutoResponse;
import com.github.joseprandj.OrderFlow_Produto.exception.ProdutoNaoEncontradoException;
import com.github.joseprandj.OrderFlow_Produto.exception.SkuJaCadastradoException;
import com.github.joseprandj.OrderFlow_Produto.model.Produto;
import com.github.joseprandj.OrderFlow_Produto.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final EstoqueClient estoqueClient;

    /**
     * Cria o produto e, em seguida, o seu estoque inicial. Caso a criação do estoque falhe,
     * a transação é revertida e o produto não é persistido.
     */
    @Transactional
    public ProdutoResponse criar(ProdutoRequest request) {
        if (produtoRepository.existsBySku(request.sku())) {
            throw new SkuJaCadastradoException(request.sku());
        }
        Produto produto = new Produto();
        produto.setSku(request.sku());
        produto.setNome(request.nome());
        produto.setDescricao(request.descricao());
        produto.setPreco(request.preco());
        produto.setAtivo(Boolean.TRUE.equals(request.ativo()));

        Produto salvo = produtoRepository.saveAndFlush(produto);
        estoqueClient.criarEstoque(salvo.getSku());
        return ProdutoResponse.from(salvo);
    }

    @Transactional(readOnly = true)
    public Page<ProdutoResponse> listar(Boolean ativo, Pageable pageable) {
        Page<Produto> produtos = ativo == null
                ? produtoRepository.findAll(pageable)
                : produtoRepository.findByAtivo(ativo, pageable);
        return produtos.map(ProdutoResponse::from);
    }

    @Transactional(readOnly = true)
    public ProdutoResponse buscarPorId(UUID id) {
        return ProdutoResponse.from(obterProduto(id));
    }

    @Transactional
    public ProdutoResponse atualizar(UUID id, ProdutoAtualizacaoRequest request) {
        Produto produto = obterProduto(id);
        produto.setNome(request.nome());
        produto.setDescricao(request.descricao());
        produto.setPreco(request.preco());
        produto.setAtivo(request.ativo());
        return ProdutoResponse.from(produtoRepository.saveAndFlush(produto));
    }

    @Transactional
    public ProdutoResponse atualizarParcialmente(UUID id, ProdutoAtualizacaoParcialRequest request) {
        Produto produto = obterProduto(id);
        if (request.nome() != null) {
            produto.setNome(request.nome());
        }
        if (request.descricao() != null) {
            produto.setDescricao(request.descricao());
        }
        if (request.preco() != null) {
            produto.setPreco(request.preco());
        }
        if (request.ativo() != null) {
            produto.setAtivo(request.ativo());
        }
        return ProdutoResponse.from(produtoRepository.saveAndFlush(produto));
    }

    @Transactional
    public void excluir(UUID id) {
        produtoRepository.delete(obterProduto(id));
    }

    private Produto obterProduto(UUID id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ProdutoNaoEncontradoException(id));
    }
}
