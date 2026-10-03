package com.github.joseprandj.OrderFlow_Logistica.service;

import com.github.joseprandj.OrderFlow_Logistica.dto.LogisticaRequest;
import com.github.joseprandj.OrderFlow_Logistica.dto.LogisticaResponse;
import com.github.joseprandj.OrderFlow_Logistica.dto.LogisticaStatusRequest;
import com.github.joseprandj.OrderFlow_Logistica.exception.LogisticaNaoEncontradaException;
import com.github.joseprandj.OrderFlow_Logistica.exception.PedidoJaPossuiLogisticaException;
import com.github.joseprandj.OrderFlow_Logistica.model.Logistica;
import com.github.joseprandj.OrderFlow_Logistica.model.StatusLogistica;
import com.github.joseprandj.OrderFlow_Logistica.repository.LogisticaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogisticaServiceTest {

    @Mock
    private LogisticaRepository logisticaRepository;

    @InjectMocks
    private LogisticaService logisticaService;

    @Test
    void criarDeveUsarStatusPendentePagamentoPorPadraoERemoverEspacos() {
        when(logisticaRepository.save(any(Logistica.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        LogisticaResponse response = logisticaService.criar(
                new LogisticaRequest(UUID.randomUUID(), null, "  Aguardando coleta  "));

        assertThat(response.status()).isEqualTo(StatusLogistica.PENDENTE_PAGAMENTO);
        assertThat(response.ocorrencia()).isEqualTo("Aguardando coleta");
    }

    @Test
    void criarDeveRespeitarStatusInformado() {
        when(logisticaRepository.save(any(Logistica.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        LogisticaResponse response = logisticaService.criar(
                new LogisticaRequest(UUID.randomUUID(), StatusLogistica.PAGO, null));

        assertThat(response.status()).isEqualTo(StatusLogistica.PAGO);
    }

    @Test
    void criarDeveRejeitarSegundoRegistroParaOMesmoPedido() {
        UUID idPedido = UUID.randomUUID();
        when(logisticaRepository.existsByIdPedido(idPedido)).thenReturn(true);

        assertThatThrownBy(() -> logisticaService.criar(new LogisticaRequest(idPedido, null, null)))
                .isInstanceOf(PedidoJaPossuiLogisticaException.class);
        verify(logisticaRepository, never()).save(any());
    }

    @Test
    void alterarStatusSemOcorrenciaDeveLimparOcorrencia() {
        Logistica logistica = logisticaExistente();
        when(logisticaRepository.findById(logistica.getId())).thenReturn(Optional.of(logistica));
        when(logisticaRepository.saveAndFlush(logistica)).thenReturn(logistica);

        LogisticaResponse response = logisticaService.alterarStatus(logistica.getId(),
                new LogisticaStatusRequest(StatusLogistica.ENTREGUE, null));

        assertThat(response.status()).isEqualTo(StatusLogistica.ENTREGUE);
        assertThat(response.ocorrencia()).isNull();
    }

    @Test
    void alterarStatusComOcorrenciaDeveAlterarAmbos() {
        Logistica logistica = logisticaExistente();
        UUID idPedido = logistica.getIdPedido();
        when(logisticaRepository.findById(logistica.getId())).thenReturn(Optional.of(logistica));
        when(logisticaRepository.saveAndFlush(logistica)).thenReturn(logistica);

        LogisticaResponse response = logisticaService.alterarStatus(logistica.getId(),
                new LogisticaStatusRequest(StatusLogistica.RECUSADA, "  Destinatário ausente  "));

        assertThat(response.status()).isEqualTo(StatusLogistica.RECUSADA);
        assertThat(response.ocorrencia()).isEqualTo("Destinatário ausente");
        assertThat(response.idPedido()).isEqualTo(idPedido);
    }

    private static Logistica logisticaExistente() {
        Logistica logistica = new Logistica();
        logistica.setId(UUID.randomUUID());
        logistica.setIdPedido(UUID.randomUUID());
        logistica.setOcorrencia("Ocorrência");
        return logistica;
    }

    @Test
    void excluirDeveLancarExcecaoQuandoNaoEncontrado() {
        UUID id = UUID.randomUUID();
        when(logisticaRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> logisticaService.excluir(id))
                .isInstanceOf(LogisticaNaoEncontradaException.class);
    }
}
