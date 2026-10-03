package com.github.joseprandj.OrderFlow_Pedido.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "itens_pedido", uniqueConstraints = @UniqueConstraint(columnNames = {"pedido_id", "sku"}))
@Getter
@Setter
@NoArgsConstructor
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @Column(name = "id_produto", nullable = false, updatable = false)
    private UUID idProduto;

    @Column(nullable = false, updatable = false)
    private String sku;

    @Column(nullable = false, updatable = false, precision = 19, scale = 2)
    private BigDecimal preco;

    @Setter(AccessLevel.NONE)
    @Column(nullable = false)
    private int quantidade;

    @Setter(AccessLevel.NONE)
    @Column(name = "valor_total", nullable = false, precision = 19, scale = 2)
    private BigDecimal valorTotal;

    @Column(name = "data_hora_criacao", nullable = false, updatable = false)
    private LocalDateTime dataHoraCriacao;

    @Column(name = "data_hora_alteracao")
    private LocalDateTime dataHoraAlteracao;

    public ItemPedido(UUID idProduto, String sku, BigDecimal preco, int quantidade) {
        this.idProduto = idProduto;
        this.sku = sku;
        this.preco = preco;
        definirQuantidade(quantidade);
    }

    /**
     * Altera a quantidade e recalcula o valor total do item com base no preço registrado na inclusão.
     */
    public void definirQuantidade(int quantidade) {
        this.quantidade = quantidade;
        this.valorTotal = preco.multiply(BigDecimal.valueOf(quantidade));
    }

    @PrePersist
    void aoCriar() {
        dataHoraCriacao = LocalDateTime.now();
    }

    @PreUpdate
    void aoAlterar() {
        dataHoraAlteracao = LocalDateTime.now();
    }
}
