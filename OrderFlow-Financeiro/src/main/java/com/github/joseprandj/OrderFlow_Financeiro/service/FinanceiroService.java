package com.github.joseprandj.OrderFlow_Financeiro.service;

import com.github.joseprandj.OrderFlow_Financeiro.dto.FinanceiroAtualizacaoRequest;
import com.github.joseprandj.OrderFlow_Financeiro.dto.FinanceiroRequest;
import com.github.joseprandj.OrderFlow_Financeiro.dto.FinanceiroResponse;
import com.github.joseprandj.OrderFlow_Financeiro.exception.FinanceiroNaoEncontradoException;
import com.github.joseprandj.OrderFlow_Financeiro.exception.PedidoJaPossuiFinanceiroException;
import com.github.joseprandj.OrderFlow_Financeiro.model.Financeiro;
import com.github.joseprandj.OrderFlow_Financeiro.repository.FinanceiroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FinanceiroService {

    private final FinanceiroRepository financeiroRepository;

    @Transactional
    public FinanceiroResponse criar(FinanceiroRequest request) {
        if (financeiroRepository.existsByIdPedido(request.idPedido())) {
            throw new PedidoJaPossuiFinanceiroException(request.idPedido());
        }
        Financeiro financeiro = new Financeiro();
        financeiro.setIdPedido(request.idPedido());
        financeiro.setValor(request.valor());
        financeiro.setStatus(request.status());
        return FinanceiroResponse.from(financeiroRepository.save(financeiro));
    }

    @Transactional(readOnly = true)
    public Page<FinanceiroResponse> listar(Pageable pageable) {
        return financeiroRepository.findAll(pageable).map(FinanceiroResponse::from);
    }

    @Transactional(readOnly = true)
    public FinanceiroResponse buscarPorId(UUID id) {
        return FinanceiroResponse.from(obterFinanceiro(id));
    }

    @Transactional
    public FinanceiroResponse atualizar(UUID id, FinanceiroAtualizacaoRequest request) {
        Financeiro financeiro = obterFinanceiro(id);
        financeiro.setValor(request.valor());
        financeiro.setStatus(request.status());
        return FinanceiroResponse.from(financeiroRepository.saveAndFlush(financeiro));
    }

    @Transactional
    public void excluir(UUID id) {
        financeiroRepository.delete(obterFinanceiro(id));
    }

    private Financeiro obterFinanceiro(UUID id) {
        return financeiroRepository.findById(id)
                .orElseThrow(() -> new FinanceiroNaoEncontradoException(id));
    }
}
