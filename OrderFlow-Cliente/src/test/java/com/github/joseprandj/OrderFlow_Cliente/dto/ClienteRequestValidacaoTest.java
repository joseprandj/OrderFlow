package com.github.joseprandj.OrderFlow_Cliente.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ClienteRequestValidacaoTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void configurar() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void finalizar() {
        validatorFactory.close();
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345678901", "12345678000190", " 12345678901 "})
    void cpfCnpjSomenteComDigitosEhValido(String cpfCnpj) {
        assertThat(violacoesDeCpfCnpj(requestCom(cpfCnpj))).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"123.456.789-01", "12.345.678/0001-90", "1234567890", "123456789012", "abc45678901"})
    void cpfCnpjComMascaraOuQuantidadeDeDigitosInvalidaEhRejeitado(String cpfCnpj) {
        assertThat(violacoesDeCpfCnpj(requestCom(cpfCnpj))).isNotEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"123.456.789-01", "1234567890"})
    void atualizacaoParcialRejeitaCpfCnpjInvalido(String cpfCnpj) {
        var request = new ClienteAtualizacaoParcialRequest(cpfCnpj, null, null, null, null);

        assertThat(violacoesDeCpfCnpj(request)).isNotEmpty();
    }

    private static <T> Set<ConstraintViolation<T>> violacoesDeCpfCnpj(T request) {
        return validator.validateProperty(request, "cpfCnpj");
    }

    private static ClienteRequest requestCom(String cpfCnpj) {
        return new ClienteRequest(cpfCnpj, "Maria", "11999999999", "maria@email.com", null);
    }
}
