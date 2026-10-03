package com.github.joseprandj.OrderFlow_Financeiro.service;

import com.github.joseprandj.OrderFlow_Financeiro.dto.FinanceiroAtualizacaoRequest;
import com.github.joseprandj.OrderFlow_Financeiro.dto.FinanceiroRequest;
import com.github.joseprandj.OrderFlow_Financeiro.dto.FinanceiroResponse;
import com.github.joseprandj.OrderFlow_Financeiro.exception.FinanceiroNaoEncontradoException;
import com.github.joseprandj.OrderFlow_Financeiro.exception.PedidoJaPossuiFinanceiroException;
import com.github.joseprandj.OrderFlow_Financeiro.model.Financeiro;
import com.github.joseprandj.OrderFlow_Financeiro.model.StatusFinanceiro;
import com.github.joseprandj.OrderFlow_Financeiro.repository.FinanceiroRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FinanceiroServiceTest {

    @Mock
    private FinanceiroRepository financeiroRepository;

    @InjectMocks
    private FinanceiroService financeiroService;

    @Test
    void criarDeveAssociarFinanceiroAoPedido() {
        UUID idPedido = UUID.randomUUID();
        when(financeiroRepository.save(any(Financeiro.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        FinanceiroResponse response = financeiroService.criar(
                new FinanceiroRequest(idPedido, new BigDecimal("100.00"), StatusFinanceiro.PENDENTE));

        assertThat(response.idPedido()).isEqualTo(idPedido);
        assertThat(response.valor()).isEqualByComparingTo("100.00");
        assertThat(response.status()).isEqualTo(StatusFinanceiro.PENDENTE);
    }

    @Test
    void criarDeveRejeitarSegundoFinanceiroParaOMesmoPedido() {
        UUID idPedido = UUID.randomUUID();
        when(financeiroRepository.existsByIdPedido(idPedido)).thenReturn(true);

        assertThatThrownBy(() -> financeiroService.criar(
                new FinanceiroRequest(idPedido, BigDecimal.TEN, StatusFinanceiro.PENDENTE)))
                .isInstanceOf(PedidoJaPossuiFinanceiroException.class);
        verify(financeiroRepository, never()).save(any());
    }

    @Test
    void atualizarDeveAlterarValorEStatusMantendoPedido() {
        Financeiro financeiro = new Financeiro();
        financeiro.setId(UUID.randomUUID());
        financeiro.setIdPedido(UUID.randomUUID());
        financeiro.setValor(BigDecimal.TEN);
        financeiro.setStatus(StatusFinanceiro.PENDENTE);
        when(financeiroRepository.findById(financeiro.getId())).thenReturn(Optional.of(financeiro));
        when(financeiroRepository.saveAndFlush(financeiro)).thenReturn(financeiro);

        FinanceiroResponse response = financeiroService.atualizar(financeiro.getId(),
                new FinanceiroAtualizacaoRequest(new BigDecimal("55.90"), StatusFinanceiro.APROVADO));

        assertThat(response.valor()).isEqualByComparingTo("55.90");
        assertThat(response.status()).isEqualTo(StatusFinanceiro.APROVADO);
        assertThat(response.idPedido()).isEqualTo(financeiro.getIdPedido());
    }

    @Test
    void buscarPorIdDeveLancarExcecaoQuandoNaoEncontrado() {
        UUID id = UUID.randomUUID();
        when(financeiroRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> financeiroService.buscarPorId(id))
                .isInstanceOf(FinanceiroNaoEncontradoException.class);
    }
}
