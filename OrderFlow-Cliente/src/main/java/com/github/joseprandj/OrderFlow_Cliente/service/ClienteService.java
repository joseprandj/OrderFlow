package com.github.joseprandj.OrderFlow_Cliente.service;

import com.github.joseprandj.OrderFlow_Cliente.dto.ClienteAtualizacaoParcialRequest;
import com.github.joseprandj.OrderFlow_Cliente.dto.ClienteRequest;
import com.github.joseprandj.OrderFlow_Cliente.dto.ClienteResponse;
import com.github.joseprandj.OrderFlow_Cliente.exception.ClienteNaoEncontradoException;
import com.github.joseprandj.OrderFlow_Cliente.exception.CpfCnpjJaCadastradoException;
import com.github.joseprandj.OrderFlow_Cliente.model.Cliente;
import com.github.joseprandj.OrderFlow_Cliente.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional
    public ClienteResponse criar(ClienteRequest request) {
        Cliente cliente = new Cliente();
        aplicarDados(cliente, request);
        return ClienteResponse.from(clienteRepository.save(cliente));
    }

    @Transactional(readOnly = true)
    public Page<ClienteResponse> listar(Pageable pageable) {
        return clienteRepository.findAll(pageable).map(ClienteResponse::from);
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscarPorCpfCnpj(String cpfCnpj) {
        return clienteRepository.findByCpfCnpj(cpfCnpj)
                .map(ClienteResponse::from)
                .orElseThrow(() -> new ClienteNaoEncontradoException(cpfCnpj));
    }

    @Transactional
    public ClienteResponse atualizar(UUID id, ClienteRequest request) {
        Cliente cliente = obterCliente(id);
        aplicarDados(cliente, request);
        return ClienteResponse.from(clienteRepository.saveAndFlush(cliente));
    }

    @Transactional
    public ClienteResponse atualizarParcialmente(UUID id, ClienteAtualizacaoParcialRequest request) {
        Cliente cliente = obterCliente(id);
        if (request.cpfCnpj() != null) {
            definirCpfCnpj(cliente, request.cpfCnpj());
        }
        if (request.nome() != null) {
            cliente.setNome(request.nome());
        }
        if (request.telefone() != null) {
            cliente.setTelefone(request.telefone());
        }
        if (request.email() != null) {
            cliente.setEmail(request.email());
        }
        if (request.endereco() != null) {
            cliente.setEndereco(request.endereco());
        }
        return ClienteResponse.from(clienteRepository.saveAndFlush(cliente));
    }

    @Transactional
    public void excluir(UUID id) {
        clienteRepository.delete(obterCliente(id));
    }

    private Cliente obterCliente(UUID id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNaoEncontradoException(id.toString()));
    }

    private void aplicarDados(Cliente cliente, ClienteRequest request) {
        definirCpfCnpj(cliente, request.cpfCnpj());
        cliente.setNome(request.nome());
        cliente.setTelefone(request.telefone());
        cliente.setEmail(request.email());
        cliente.setEndereco(request.endereco());
    }

    /**
     * O formato do {@code cpfCnpj} (somente dígitos, 11 ou 14 posições) é validado no DTO de entrada.
     */
    private void definirCpfCnpj(Cliente cliente, String cpfCnpj) {
        if (!cpfCnpj.equals(cliente.getCpfCnpj()) && clienteRepository.existsByCpfCnpj(cpfCnpj)) {
            throw new CpfCnpjJaCadastradoException(cpfCnpj);
        }
        cliente.setCpfCnpj(cpfCnpj);
        cliente.setTipo(cpfCnpj.length() == Cliente.DIGITOS_CPF ? Cliente.TIPO_PESSOA_FISICA : Cliente.TIPO_PESSOA_JURIDICA);
    }
}
