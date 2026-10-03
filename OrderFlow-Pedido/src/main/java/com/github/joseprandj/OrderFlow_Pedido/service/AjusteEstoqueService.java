package com.github.joseprandj.OrderFlow_Pedido.service;

import com.github.joseprandj.OrderFlow_Pedido.client.EstoqueClient;
import com.github.joseprandj.OrderFlow_Pedido.exception.EstoqueInsuficienteException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Aplica no domínio Estoque as variações de quantidade decorrentes das operações do pedido.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AjusteEstoqueService {

    private final EstoqueClient estoqueClient;

    /**
     * Aplica os ajustes em sequência. Se algum ajuste falhar, os ajustes já aplicados são revertidos
     * e a exceção original é propagada.
     *
     * @throws EstoqueInsuficienteException quando a quantidade debitada é superior à disponível
     */
    public void aplicar(List<AjusteEstoque> ajustes) {
        List<AjusteEstoque> aplicados = new ArrayList<>();
        try {
            for (AjusteEstoque ajuste : ajustes) {
                aplicar(ajuste);
                aplicados.add(ajuste);
            }
        } catch (RuntimeException e) {
            reverter(aplicados);
            throw e;
        }
    }

    private void aplicar(AjusteEstoque ajuste) {
        if (ajuste.quantidadeDebitada() == 0) {
            return;
        }
        int disponivel = estoqueClient.consultarQuantidade(ajuste.sku());
        int novaQuantidade = disponivel - ajuste.quantidadeDebitada();
        if (novaQuantidade < 0) {
            throw new EstoqueInsuficienteException(ajuste.sku(), disponivel, ajuste.quantidadeDebitada());
        }
        estoqueClient.atualizarQuantidade(ajuste.sku(), novaQuantidade);
    }

    private void reverter(List<AjusteEstoque> aplicados) {
        for (AjusteEstoque ajuste : aplicados.reversed()) {
            try {
                aplicar(ajuste.inverso());
            } catch (RuntimeException e) {
                log.error("Não foi possível reverter o ajuste de estoque {}.", ajuste, e);
            }
        }
    }
}
