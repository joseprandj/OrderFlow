package com.github.joseprandj.OrderFlow_Logistica.service;

import com.github.joseprandj.OrderFlow_Logistica.dto.LogisticaRequest;
import com.github.joseprandj.OrderFlow_Logistica.dto.LogisticaResponse;
import com.github.joseprandj.OrderFlow_Logistica.dto.LogisticaStatusRequest;
import com.github.joseprandj.OrderFlow_Logistica.exception.LogisticaNaoEncontradaException;
import com.github.joseprandj.OrderFlow_Logistica.exception.PedidoJaPossuiLogisticaException;
import com.github.joseprandj.OrderFlow_Logistica.model.Logistica;
import com.github.joseprandj.OrderFlow_Logistica.repository.LogisticaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LogisticaService {

    private final LogisticaRepository logisticaRepository;

    @Transactional
    public LogisticaResponse criar(LogisticaRequest request) {
        if (logisticaRepository.existsByIdPedido(request.idPedido())) {
            throw new PedidoJaPossuiLogisticaException(request.idPedido());
        }
        Logistica logistica = new Logistica();
        logistica.setIdPedido(request.idPedido());
        logistica.setOcorrencia(request.ocorrencia());
        if (request.status() != null) {
            logistica.setStatus(request.status());
        }
        return LogisticaResponse.from(logisticaRepository.save(logistica));
    }

    @Transactional(readOnly = true)
    public Page<LogisticaResponse> listar(Pageable pageable) {
        return logisticaRepository.findAll(pageable).map(LogisticaResponse::from);
    }

    @Transactional(readOnly = true)
    public LogisticaResponse buscarPorId(UUID id) {
        return LogisticaResponse.from(obterLogistica(id));
    }

    @Transactional
    public LogisticaResponse alterarStatus(UUID id, LogisticaStatusRequest request) {
        Logistica logistica = obterLogistica(id);
        logistica.setStatus(request.status());
        logistica.setOcorrencia(request.ocorrencia());
        return LogisticaResponse.from(logisticaRepository.saveAndFlush(logistica));
    }

    @Transactional
    public void excluir(UUID id) {
        logisticaRepository.delete(obterLogistica(id));
    }

    private Logistica obterLogistica(UUID id) {
        return logisticaRepository.findById(id)
                .orElseThrow(() -> new LogisticaNaoEncontradaException(id));
    }
}
