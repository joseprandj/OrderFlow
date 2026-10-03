package com.github.joseprandj.OrderFlow_Logistica.exception;

import org.springframework.http.HttpStatusCode;

import java.time.LocalDateTime;
import java.util.List;

public record ErroResponse(
        LocalDateTime dataHora,
        int status,
        String mensagem,
        List<CampoErro> erros
) {

    public static ErroResponse of(HttpStatusCode status, String mensagem) {
        return of(status, mensagem, List.of());
    }

    public static ErroResponse of(HttpStatusCode status, String mensagem, List<CampoErro> erros) {
        return new ErroResponse(LocalDateTime.now(), status.value(), mensagem, erros);
    }
}
