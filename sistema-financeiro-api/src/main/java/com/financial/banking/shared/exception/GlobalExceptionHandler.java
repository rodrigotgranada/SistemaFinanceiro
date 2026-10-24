package com.financial.banking.shared.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ContaNaoEncontradaException.class)
    public ProblemDetail handleContaNaoEncontrada(ContaNaoEncontradaException ex) {
        log.warn("Conta não encontrada: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Conta Corrente Não Encontrada");
        problem.setType(URI.create("https://api.banking.com/errors/conta-nao-encontrada"));
        problem.setProperty("timestamp", OffsetDateTime.now());
        return problem;
    }

    @ExceptionHandler(SaldoInsuficienteException.class)
    public ProblemDetail handleSaldoInsuficiente(SaldoInsuficienteException ex) {
        log.warn("Saldo insuficiente: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setTitle("Saldo Insuficiente");
        problem.setType(URI.create("https://api.banking.com/errors/saldo-insuficiente"));
        problem.setProperty("timestamp", OffsetDateTime.now());
        return problem;
    }

    @ExceptionHandler({ValorInvalidoException.class, DocumentoInvalidoException.class, TransferenciaMesmaContaException.class})
    public ProblemDetail handleRequisicaoInvalida(BusinessException ex) {
        log.warn("Erro de validação de negócio: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Regra de Negócio Inválida");
        problem.setType(URI.create("https://api.banking.com/errors/regra-invalida"));
        problem.setProperty("timestamp", OffsetDateTime.now());
        return problem;
    }

    @ExceptionHandler(ContaJaExisteException.class)
    public ProblemDetail handleContaJaExiste(ContaJaExisteException ex) {
        log.warn("Tentativa de criar conta duplicada: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Conta Corrente Já Existente");
        problem.setType(URI.create("https://api.banking.com/errors/conta-ja-existe"));
        problem.setProperty("timestamp", OffsetDateTime.now());
        return problem;
    }

    @ExceptionHandler(ContaInativaException.class)
    public ProblemDetail handleContaInativa(ContaInativaException ex) {
        log.warn("Conta inativa: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problem.setTitle("Conta Corrente Inativa");
        problem.setType(URI.create("https://api.banking.com/errors/conta-inativa"));
        problem.setProperty("timestamp", OffsetDateTime.now());
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        log.warn("Erro de validação nos campos da requisição: {}", errors);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Um ou mais campos contêm valores inválidos");
        problem.setTitle("Erro de Validação de Dados");
        problem.setType(URI.create("https://api.banking.com/errors/validacao"));
        problem.setProperty("timestamp", OffsetDateTime.now());
        problem.setProperty("errors", errors);
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneralError(Exception ex) {
        log.error("Erro interno inesperado no servidor: ", ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro interno na operação. Tente novamente mais tarde.");
        problem.setTitle("Erro Interno do Servidor");
        problem.setType(URI.create("https://api.banking.com/errors/internal-error"));
        problem.setProperty("timestamp", OffsetDateTime.now());
        return problem;
    }
}
