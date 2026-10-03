package com.github.joseprandj.OrderFlow_Estoque.service;

import com.github.joseprandj.OrderFlow_Estoque.dto.EstoqueCriacaoRequest;
import com.github.joseprandj.OrderFlow_Estoque.dto.EstoqueQuantidadeRequest;
import com.github.joseprandj.OrderFlow_Estoque.dto.EstoqueResponse;
import com.github.joseprandj.OrderFlow_Estoque.exception.EstoqueNaoEncontradoException;
import com.github.joseprandj.OrderFlow_Estoque.exception.SkuJaCadastradoException;
import com.github.joseprandj.OrderFlow_Estoque.model.Estoque;
import com.github.joseprandj.OrderFlow_Estoque.repository.EstoqueRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstoqueServiceTest {

    @Mock
    private EstoqueRepository estoqueRepository;

    @InjectMocks
    private EstoqueService estoqueService;

    @Test
    void criarDeveIniciarComQuantidadeZeroERemoverEspacosDoSku() {
        when(estoqueRepository.save(any(Estoque.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        EstoqueResponse response = estoqueService.criar(new EstoqueCriacaoRequest("  SKU-1  "));

        assertThat(response.sku()).isEqualTo("SKU-1");
        assertThat(response.quantidade()).isZero();
    }

    @Test
    void criarDeveRejeitarSkuDuplicado() {
        when(estoqueRepository.existsBySku("SKU-1")).thenReturn(true);

        assertThatThrownBy(() -> estoqueService.criar(new EstoqueCriacaoRequest("SKU-1")))
                .isInstanceOf(SkuJaCadastradoException.class);
        verify(estoqueRepository, never()).save(any());
    }

    @Test
    void listarSemFiltroDeveRetornarTodos() {
        Pageable pageable = PageRequest.of(0, 10);
        when(estoqueRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(estoque("SKU-1", 5))));

        Page<EstoqueResponse> pagina = estoqueService.listar(null, pageable);

        assertThat(pagina.getContent()).hasSize(1);
        verify(estoqueRepository, never()).findByQuantidadeGreaterThanEqual(any(Integer.class), any());
    }

    @Test
    void listarComQuantidadeMinimaDeveFiltrarPorQuantidadeMaiorOuIgual() {
        Pageable pageable = PageRequest.of(0, 10);
        when(estoqueRepository.findByQuantidadeGreaterThanEqual(3, pageable))
                .thenReturn(new PageImpl<>(List.of(estoque("SKU-1", 5))));

        Page<EstoqueResponse> pagina = estoqueService.listar(3, pageable);

        assertThat(pagina.getContent()).extracting(EstoqueResponse::quantidade).containsExactly(5);
    }

    @Test
    void atualizarQuantidadeDeveAlterarSomenteAQuantidade() {
        Estoque estoque = estoque("SKU-1", 5);
        when(estoqueRepository.findBySku("SKU-1")).thenReturn(Optional.of(estoque));
        when(estoqueRepository.saveAndFlush(estoque)).thenReturn(estoque);

        EstoqueResponse response = estoqueService.atualizarQuantidade(" SKU-1 ", new EstoqueQuantidadeRequest(12));

        assertThat(response.quantidade()).isEqualTo(12);
        assertThat(response.sku()).isEqualTo("SKU-1");
    }

    @Test
    void buscarPorSkuDeveLancarExcecaoQuandoNaoEncontrado() {
        when(estoqueRepository.findBySku("SKU-X")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> estoqueService.buscarPorSku("SKU-X"))
                .isInstanceOf(EstoqueNaoEncontradoException.class);
    }

    private static Estoque estoque(String sku, int quantidade) {
        Estoque estoque = new Estoque();
        estoque.setSku(sku);
        estoque.setQuantidade(quantidade);
        return estoque;
    }
}
