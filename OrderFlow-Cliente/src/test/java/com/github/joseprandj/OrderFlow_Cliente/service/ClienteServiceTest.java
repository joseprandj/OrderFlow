package com.github.joseprandj.OrderFlow_Cliente.service;

import com.github.joseprandj.OrderFlow_Cliente.dto.ClienteAtualizacaoParcialRequest;
import com.github.joseprandj.OrderFlow_Cliente.dto.ClienteRequest;
import com.github.joseprandj.OrderFlow_Cliente.dto.ClienteResponse;
import com.github.joseprandj.OrderFlow_Cliente.exception.ClienteNaoEncontradoException;
import com.github.joseprandj.OrderFlow_Cliente.exception.CpfCnpjJaCadastradoException;
import com.github.joseprandj.OrderFlow_Cliente.model.Cliente;
import com.github.joseprandj.OrderFlow_Cliente.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    void criarDeveDefinirTipoPessoaFisicaParaCpf() {
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        ClienteResponse response = clienteService.criar(requestCom("12345678901"));

        assertThat(response.cpfCnpj()).isEqualTo("12345678901");
        assertThat(response.tipo()).isEqualTo('F');
    }

    @Test
    void criarDeveDefinirTipoPessoaJuridicaParaCnpj() {
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        ClienteResponse response = clienteService.criar(requestCom("12345678000190"));

        assertThat(response.cpfCnpj()).isEqualTo("12345678000190");
        assertThat(response.tipo()).isEqualTo('J');
    }

    @Test
    void criarDeveRemoverEspacosDosCampos() {
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        ClienteResponse response = clienteService.criar(
                new ClienteRequest(" 12345678901 ", "  Maria  ", " 1199999 ", " maria@email.com ", " Rua A "));

        assertThat(response.nome()).isEqualTo("Maria");
        assertThat(response.telefone()).isEqualTo("1199999");
        assertThat(response.email()).isEqualTo("maria@email.com");
        assertThat(response.endereco()).isEqualTo("Rua A");
    }

    @Test
    void criarDeveRejeitarCpfCnpjDuplicado() {
        when(clienteRepository.existsByCpfCnpj("12345678901")).thenReturn(true);

        assertThatThrownBy(() -> clienteService.criar(requestCom("12345678901")))
                .isInstanceOf(CpfCnpjJaCadastradoException.class);
        verify(clienteRepository, never()).save(any());
    }

    @Test
    void atualizarDevePermitirManterOProprioCpfCnpj() {
        Cliente existente = clienteExistente("12345678901");
        when(clienteRepository.findById(existente.getId())).thenReturn(Optional.of(existente));
        when(clienteRepository.saveAndFlush(existente)).thenReturn(existente);

        ClienteResponse response = clienteService.atualizar(existente.getId(),
                new ClienteRequest("12345678901", "Novo Nome", "11888888", "novo@email.com", null));

        assertThat(response.nome()).isEqualTo("Novo Nome");
        verify(clienteRepository, never()).existsByCpfCnpj(any());
    }

    @Test
    void atualizarDeveRecalcularTipoAoAlterarCpfCnpj() {
        Cliente existente = clienteExistente("12345678901");
        when(clienteRepository.findById(existente.getId())).thenReturn(Optional.of(existente));
        when(clienteRepository.saveAndFlush(existente)).thenReturn(existente);

        ClienteResponse response = clienteService.atualizar(existente.getId(), requestCom("12345678000190"));

        assertThat(response.tipo()).isEqualTo('J');
    }

    @Test
    void atualizarParcialmenteDeveAlterarSomenteCamposInformados() {
        Cliente existente = clienteExistente("12345678901");
        when(clienteRepository.findById(existente.getId())).thenReturn(Optional.of(existente));
        when(clienteRepository.saveAndFlush(existente)).thenReturn(existente);

        ClienteResponse response = clienteService.atualizarParcialmente(existente.getId(),
                new ClienteAtualizacaoParcialRequest(null, " Outro Nome ", null, null, null));

        assertThat(response.nome()).isEqualTo("Outro Nome");
        assertThat(response.email()).isEqualTo("antigo@email.com");
        assertThat(response.cpfCnpj()).isEqualTo("12345678901");
    }

    @Test
    void buscarPorCpfCnpjDeveLancarExcecaoQuandoNaoEncontrado() {
        when(clienteRepository.findByCpfCnpj("99999999999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.buscarPorCpfCnpj("99999999999"))
                .isInstanceOf(ClienteNaoEncontradoException.class);
    }

    @Test
    void atualizarDeveLancarExcecaoQuandoClienteNaoEncontrado() {
        UUID id = UUID.randomUUID();
        when(clienteRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.atualizar(id, requestCom("12345678901")))
                .isInstanceOf(ClienteNaoEncontradoException.class);
    }

    @Test
    void buscarPorCpfCnpjDeveRetornarClienteCadastrado() {
        Cliente existente = clienteExistente("12345678901");
        when(clienteRepository.findByCpfCnpj("12345678901")).thenReturn(Optional.of(existente));

        ClienteResponse response = clienteService.buscarPorCpfCnpj("12345678901");

        assertThat(response.id()).isEqualTo(existente.getId());
    }

    @Test
    void excluirDeveRemoverClienteExistente() {
        Cliente existente = clienteExistente("12345678901");
        when(clienteRepository.findById(existente.getId())).thenReturn(Optional.of(existente));

        clienteService.excluir(existente.getId());

        ArgumentCaptor<Cliente> captor = ArgumentCaptor.forClass(Cliente.class);
        verify(clienteRepository).delete(captor.capture());
        assertThat(captor.getValue()).isSameAs(existente);
    }

    private static ClienteRequest requestCom(String cpfCnpj) {
        return new ClienteRequest(cpfCnpj, "Maria", "11999999999", "maria@email.com", "Rua A");
    }

    private static Cliente clienteExistente(String cpfCnpj) {
        Cliente cliente = new Cliente();
        cliente.setId(UUID.randomUUID());
        cliente.setCpfCnpj(cpfCnpj);
        cliente.setTipo(Cliente.TIPO_PESSOA_FISICA);
        cliente.setNome("Nome Antigo");
        cliente.setTelefone("11999999999");
        cliente.setEmail("antigo@email.com");
        return cliente;
    }
}
