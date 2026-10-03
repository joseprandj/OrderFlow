package com.github.joseprandj.OrderFlow_Estoque.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "estoques")
@Getter
@Setter
@NoArgsConstructor
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, updatable = false)
    private String sku;

    @Column(nullable = false)
    private int quantidade;

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
