package com.github.joseprandj.OrderFlow_Pedido.exception;

/**
 * Falha de comunicação com outro domínio da plataforma.
 */
public class IntegracaoException extends RuntimeException {

    public IntegracaoException(String dominio, Throwable causa) {
        super("Falha na comunicação com o domínio " + dominio + ". Tente novamente mais tarde.", causa);
    }
}
