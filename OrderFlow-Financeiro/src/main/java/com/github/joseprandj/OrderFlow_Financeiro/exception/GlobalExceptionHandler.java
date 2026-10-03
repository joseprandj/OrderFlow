package com.github.joseprandj.OrderFlow_Financeiro.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String MENSAGEM_ERRO_INESPERADO = "Ocorreu um erro inesperado. Tente novamente mais tarde.";

    @ExceptionHandler(FinanceiroNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> handleFinanceiroNaoEncontrado(FinanceiroNaoEncontradoException ex) {
        return construirResposta(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(PedidoJaPossuiFinanceiroException.class)
    public ResponseEntity<ErroResponse> handlePedidoJaPossuiFinanceiro(PedidoJaPossuiFinanceiroException ex) {
        return construirResposta(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> handleExcecaoInesperada(Exception ex) {
        log.error("Erro inesperado ao processar a requisição.", ex);
        return construirResposta(HttpStatus.INTERNAL_SERVER_ERROR, MENSAGEM_ERRO_INESPERADO);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                                  HttpStatusCode status, WebRequest request) {
        List<CampoErro> erros = ex.getBindingResult().getFieldErrors().stream()
                .map(erro -> new CampoErro(erro.getField(), erro.getDefaultMessage()))
                .toList();
        return ResponseEntity.status(status).body(ErroResponse.of(status, "Um ou mais campos estão inválidos.", erros));
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatusCode statusCode, WebRequest request) {
        return ResponseEntity.status(statusCode).headers(headers).body(ErroResponse.of(statusCode, mensagemPadrao(statusCode)));
    }

    private ResponseEntity<ErroResponse> construirResposta(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(ErroResponse.of(status, mensagem));
    }

    private static String mensagemPadrao(HttpStatusCode status) {
        return switch (status.value()) {
            case 400 -> "Requisição inválida.";
            case 404 -> "Recurso não encontrado.";
            case 405 -> "Método HTTP não suportado para este recurso.";
            case 406 -> "Formato de resposta não suportado.";
            case 415 -> "Tipo de conteúdo não suportado.";
            default -> status.is5xxServerError() ? MENSAGEM_ERRO_INESPERADO : "Não foi possível processar a requisição.";
        };
    }
}
