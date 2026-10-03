package com.github.joseprandj.OrderFlow_Cliente.model;

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
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
public class Cliente {

    public static final char TIPO_PESSOA_FISICA = 'F';
    public static final char TIPO_PESSOA_JURIDICA = 'J';
    public static final int DIGITOS_CPF = 11;
    public static final String FORMATO_CPF_CNPJ = "\\d{11}|\\d{14}";
    public static final String MENSAGEM_CPF_CNPJ_INVALIDO = "deve possuir somente dígitos: 11 (CPF) ou 14 (CNPJ)";

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "cpf_cnpj", nullable = false, unique = true, length = 14)
    private String cpfCnpj;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, columnDefinition = "char(1)")
    private Character tipo;

    @Column(nullable = false)
    private String telefone;

    @Column(nullable = false)
    private String email;

    private String endereco;

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
