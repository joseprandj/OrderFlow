package com.github.joseprandj.OrderFlow_Cliente.dto;

import com.github.joseprandj.OrderFlow_Cliente.model.Cliente;

import java.time.LocalDateTime;
import java.util.UUID;

public record ClienteResponse(
        UUID id,
        String cpfCnpj,
        String nome,
        Character tipo,
        String telefone,
        String email,
        String endereco,
        LocalDateTime dataHoraCriacao,
        LocalDateTime dataHoraAlteracao
) {

    public static ClienteResponse from(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getCpfCnpj(),
                cliente.getNome(),
                cliente.getTipo(),
                cliente.getTelefone(),
                cliente.getEmail(),
                cliente.getEndereco(),
                cliente.getDataHoraCriacao(),
                cliente.getDataHoraAlteracao()
        );
    }
}
