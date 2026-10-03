package com.github.joseprandj.OrderFlow_Pedido.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "pedidos")
@Getter
@Setter
@NoArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "id_cliente", nullable = false, updatable = false)
    private UUID idCliente;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @BatchSize(size = 50)
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();

    @Setter(AccessLevel.NONE)
    @Column(name = "valor_total", nullable = false, precision = 19, scale = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Column(name = "id_financeiro")
    private UUID idFinanceiro;

    @Column(name = "id_logistica")
    private UUID idLogistica;

    @Column(name = "data_hora_criacao", nullable = false, updatable = false)
    private LocalDateTime dataHoraCriacao;

    @Column(name = "data_hora_alteracao")
    private LocalDateTime dataHoraAlteracao;

    public List<ItemPedido> getItens() {
        return Collections.unmodifiableList(itens);
    }

    public void adicionarItem(ItemPedido item) {
        item.setPedido(this);
        itens.add(item);
        recalcularValorTotal();
    }

    public void removerItem(ItemPedido item) {
        itens.remove(item);
        item.setPedido(null);
        recalcularValorTotal();
    }

    public Optional<ItemPedido> buscarItem(UUID idItem) {
        return itens.stream().filter(item -> item.getId().equals(idItem)).findFirst();
    }

    public boolean possuiItemComSku(String sku) {
        return itens.stream().anyMatch(item -> item.getSku().equals(sku));
    }

    public void recalcularValorTotal() {
        valorTotal = itens.stream()
                .map(ItemPedido::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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
