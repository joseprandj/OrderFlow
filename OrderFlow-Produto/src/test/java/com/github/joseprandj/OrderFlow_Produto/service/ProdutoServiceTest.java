package com.github.joseprandj.OrderFlow_Produto.service;

import com.github.joseprandj.OrderFlow_Produto.client.EstoqueClient;
import com.github.joseprandj.OrderFlow_Produto.dto.ProdutoAtualizacaoParcialRequest;
import com.github.joseprandj.OrderFlow_Produto.dto.ProdutoAtualizacaoRequest;
import com.github.joseprandj.OrderFlow_Produto.dto.ProdutoRequest;
import com.github.joseprandj.OrderFlow_Produto.dto.ProdutoResponse;
import com.github.joseprandj.OrderFlow_Produto.exception.IntegracaoEstoqueException;
import com.github.joseprandj.OrderFlow_Produto.exception.ProdutoNaoEncontradoException;
import com.github.joseprandj.OrderFlow_Produto.exception.SkuJaCadastradoException;
import com.github.joseprandj.OrderFlow_Produto.model.Produto;
import com.github.joseprandj.OrderFlow_Produto.repository.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private EstoqueClient estoqueClient;

    @InjectMocks
    private ProdutoService produtoService;

    @Test
    void criarDeveAplicarValoresPadraoECriarEstoque() {
        when(produtoRepository.saveAndFlush(any(Produto.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        ProdutoResponse response = produtoService.criar(
                new ProdutoRequest("  SKU-1 ", " Caneta ", null, new BigDecimal("2.50"), null));

        assertThat(response.sku()).isEqualTo("SKU-1");
        assertThat(response.nome()).isEqualTo("Caneta");
        assertThat(response.descricao()).isNull();
        assertThat(response.ativo()).isFalse();
        verify(estoqueClient).criarEstoque("SKU-1");
    }

    @Test
    void criarDeveRejeitarSkuDuplicado() {
        when(produtoRepository.existsBySku("SKU-1")).thenReturn(true);

        assertThatThrownBy(() -> produtoService.criar(
                new ProdutoRequest("SKU-1", "Caneta", null, BigDecimal.TEN, true)))
                .isInstanceOf(SkuJaCadastradoException.class);
        verify(produtoRepository, never()).saveAndFlush(any());
        verifyNoInteractions(estoqueClient);
    }

    @Test
    void criarDevePropagarFalhaDeIntegracaoComEstoque() {
        when(produtoRepository.saveAndFlush(any(Produto.class))).thenAnswer(invocacao -> invocacao.getArgument(0));
        doThrow(new IntegracaoEstoqueException(new RuntimeException())).when(estoqueClient).criarEstoque("SKU-1");

        assertThatThrownBy(() -> produtoService.criar(
                new ProdutoRequest("SKU-1", "Caneta", null, BigDecimal.TEN, true)))
                .isInstanceOf(IntegracaoEstoqueException.class);
    }

    @Test
    void listarDeveFiltrarPorAtivoQuandoInformado() {
        Pageable pageable = PageRequest.of(0, 10);
        when(produtoRepository.findByAtivo(true, pageable)).thenReturn(new PageImpl<>(List.of(produto())));

        assertThat(produtoService.listar(true, pageable).getContent()).hasSize(1);
        verify(produtoRepository, never()).findAll(pageable);
    }

    @Test
    void atualizarDeveSubstituirTodosOsCamposExcetoSku() {
        Produto produto = produto();
        when(produtoRepository.findById(produto.getId())).thenReturn(Optional.of(produto));
        when(produtoRepository.saveAndFlush(produto)).thenReturn(produto);

        ProdutoResponse response = produtoService.atualizar(produto.getId(),
                new ProdutoAtualizacaoRequest("Lápis", null, new BigDecimal("1.00"), true));

        assertThat(response.sku()).isEqualTo("SKU-1");
        assertThat(response.nome()).isEqualTo("Lápis");
        assertThat(response.descricao()).isNull();
        assertThat(response.preco()).isEqualByComparingTo("1.00");
        assertThat(response.ativo()).isTrue();
    }

    @Test
    void atualizarParcialmenteDeveAlterarSomenteCamposInformados() {
        Produto produto = produto();
        when(produtoRepository.findById(produto.getId())).thenReturn(Optional.of(produto));
        when(produtoRepository.saveAndFlush(produto)).thenReturn(produto);

        ProdutoResponse response = produtoService.atualizarParcialmente(produto.getId(),
                new ProdutoAtualizacaoParcialRequest(null, null, new BigDecimal("9.99"), null));

        assertThat(response.preco()).isEqualByComparingTo("9.99");
        assertThat(response.nome()).isEqualTo("Caneta");
        assertThat(response.descricao()).isEqualTo("Azul");
        assertThat(response.ativo()).isFalse();
    }

    @Test
    void excluirDeveLancarExcecaoQuandoProdutoNaoExiste() {
        UUID id = UUID.randomUUID();
        when(produtoRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> produtoService.excluir(id))
                .isInstanceOf(ProdutoNaoEncontradoException.class);
        verify(produtoRepository, never()).delete(any());
    }

    private static Produto produto() {
        Produto produto = new Produto();
        produto.setId(UUID.randomUUID());
        produto.setSku("SKU-1");
        produto.setNome("Caneta");
        produto.setDescricao("Azul");
        produto.setPreco(new BigDecimal("2.50"));
        produto.setAtivo(false);
        return produto;
    }
}
