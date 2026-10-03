package com.github.joseprandj.OrderFlow_Produto.dto;

import com.github.joseprandj.OrderFlow_Produto.model.Produto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProdutoResponse(
        UUID id,
        String sku,
        String nome,
        String descricao,
        BigDecimal preco,
        boolean ativo,
        LocalDateTime dataHoraCriacao,
        LocalDateTime dataHoraAlteracao
) {

    public static ProdutoResponse from(Produto produto) {
        return new ProdutoResponse(
                produto.getId(),
                produto.getSku(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.isAtivo(),
                produto.getDataHoraCriacao(),
                produto.getDataHoraAlteracao()
        );
    }
}
