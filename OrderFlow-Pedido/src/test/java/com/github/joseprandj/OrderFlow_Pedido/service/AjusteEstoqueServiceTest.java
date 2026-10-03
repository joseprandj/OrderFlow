package com.github.joseprandj.OrderFlow_Pedido.service;

import com.github.joseprandj.OrderFlow_Pedido.client.EstoqueClient;
import com.github.joseprandj.OrderFlow_Pedido.exception.EstoqueInsuficienteException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AjusteEstoqueServiceTest {

    @Mock
    private EstoqueClient estoqueClient;

    @InjectMocks
    private AjusteEstoqueService ajusteEstoqueService;

    @Test
    void debitoDeveSubtrairDaQuantidadeDisponivel() {
        when(estoqueClient.consultarQuantidade("CANETA")).thenReturn(10);

        ajusteEstoqueService.aplicar(List.of(AjusteEstoque.debito("CANETA", 4)));

        verify(estoqueClient).atualizarQuantidade("CANETA", 6);
    }

    @Test
    void debitoPermiteZerarOEstoque() {
        when(estoqueClient.consultarQuantidade("CANETA")).thenReturn(4);

        ajusteEstoqueService.aplicar(List.of(AjusteEstoque.debito("CANETA", 4)));

        verify(estoqueClient).atualizarQuantidade("CANETA", 0);
    }

    @Test
    void devolucaoDeveSomarAQuantidadeDisponivel() {
        when(estoqueClient.consultarQuantidade("CANETA")).thenReturn(1);

        ajusteEstoqueService.aplicar(List.of(AjusteEstoque.devolucao("CANETA", 3)));

        verify(estoqueClient).atualizarQuantidade("CANETA", 4);
    }

    @Test
    void ajusteSemVariacaoNaoDeveAcionarEstoque() {
        ajusteEstoqueService.aplicar(List.of(AjusteEstoque.debito("CANETA", 0)));

        verifyNoInteractions(estoqueClient);
    }

    @Test
    void debitoSuperiorAoDisponivelDeveLancarExcecaoSemAlterarEstoque() {
        when(estoqueClient.consultarQuantidade("CANETA")).thenReturn(2);

        assertThatThrownBy(() -> ajusteEstoqueService.aplicar(List.of(AjusteEstoque.debito("CANETA", 3))))
                .isInstanceOf(EstoqueInsuficienteException.class);
        verify(estoqueClient, never()).atualizarQuantidade(anyString(), anyInt());
    }

    @Test
    void falhaEmUmAjusteDeveReverterOsAjustesJaAplicados() {
        when(estoqueClient.consultarQuantidade("CANETA")).thenReturn(10, 7);
        when(estoqueClient.consultarQuantidade("CADERNO")).thenReturn(1);

        assertThatThrownBy(() -> ajusteEstoqueService.aplicar(List.of(
                AjusteEstoque.debito("CANETA", 3),
                AjusteEstoque.debito("CADERNO", 5))))
                .isInstanceOf(EstoqueInsuficienteException.class);

        verify(estoqueClient).atualizarQuantidade("CANETA", 7);
        verify(estoqueClient).atualizarQuantidade("CANETA", 10);
        verify(estoqueClient, never()).atualizarQuantidade("CADERNO", -4);
    }
}
