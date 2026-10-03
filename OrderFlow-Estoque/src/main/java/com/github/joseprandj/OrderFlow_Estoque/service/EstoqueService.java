package com.github.joseprandj.OrderFlow_Estoque.service;

import com.github.joseprandj.OrderFlow_Estoque.dto.EstoqueCriacaoRequest;
import com.github.joseprandj.OrderFlow_Estoque.dto.EstoqueQuantidadeRequest;
import com.github.joseprandj.OrderFlow_Estoque.dto.EstoqueResponse;
import com.github.joseprandj.OrderFlow_Estoque.exception.EstoqueNaoEncontradoException;
import com.github.joseprandj.OrderFlow_Estoque.exception.SkuJaCadastradoException;
import com.github.joseprandj.OrderFlow_Estoque.model.Estoque;
import com.github.joseprandj.OrderFlow_Estoque.repository.EstoqueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private static final int QUANTIDADE_INICIAL = 0;

    private final EstoqueRepository estoqueRepository;

    @Transactional
    public EstoqueResponse criar(EstoqueCriacaoRequest request) {
        if (estoqueRepository.existsBySku(request.sku())) {
            throw new SkuJaCadastradoException(request.sku());
        }
        Estoque estoque = new Estoque();
        estoque.setSku(request.sku());
        estoque.setQuantidade(QUANTIDADE_INICIAL);
        return EstoqueResponse.from(estoqueRepository.save(estoque));
    }

    @Transactional(readOnly = true)
    public Page<EstoqueResponse> listar(Integer quantidadeMinima, Pageable pageable) {
        Page<Estoque> estoques = quantidadeMinima == null
                ? estoqueRepository.findAll(pageable)
                : estoqueRepository.findByQuantidadeGreaterThanEqual(quantidadeMinima, pageable);
        return estoques.map(EstoqueResponse::from);
    }

    @Transactional(readOnly = true)
    public EstoqueResponse buscarPorSku(String sku) {
        return EstoqueResponse.from(obterEstoque(sku));
    }

    @Transactional
    public EstoqueResponse atualizarQuantidade(String sku, EstoqueQuantidadeRequest request) {
        Estoque estoque = obterEstoque(sku);
        estoque.setQuantidade(request.quantidade());
        return EstoqueResponse.from(estoqueRepository.saveAndFlush(estoque));
    }

    private Estoque obterEstoque(String sku) {
        String skuNormalizado = sku.strip();
        return estoqueRepository.findBySku(skuNormalizado)
                .orElseThrow(() -> new EstoqueNaoEncontradoException(skuNormalizado));
    }
}
