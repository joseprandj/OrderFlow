package com.github.joseprandj.OrderFlow_Logistica.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "logisticas")
@Getter
@Setter
@NoArgsConstructor
public class Logistica {

    public static final int TAMANHO_MAXIMO_OCORRENCIA = 4000;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "id_pedido", nullable = false, unique = true, updatable = false)
    private UUID idPedido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusLogistica status = StatusLogistica.PENDENTE_PAGAMENTO;

    @Column(length = TAMANHO_MAXIMO_OCORRENCIA)
    private String ocorrencia;

    @Column(name = "data_hora_criacao", nullable = false, updatable = false)
    private LocalDateTime dataHoraCriacao;

    @Column(name = "data_hora_alteracao")
    private LocalDateTime dataHoraAlteracao;

    @PrePersist
    void aoCriar() {
        dataHoraCriacao = LocalDateTime.now();
    }

    @PreUpdate
    void aoAlterar() {
        dataHoraAlteracao = LocalDateTime.now();
    }
}
