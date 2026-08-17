package com.faturamento.faturamento_core.api.exception;

import com.faturamento.faturamento_core.domain.exception.EmpresaNaoEncontradaException;
import com.faturamento.faturamento_core.domain.exception.RegraNegocioException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ResourceExceptionHandler {

    // Esse método único captura tanto o CnpjInvalido quanto a NotaDuplicada
    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<StandardError> handleRegrasDeNegocio(RegraNegocioException e, HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                e.getClass().getSimpleName(), // preenche com "CnpjInvalidoException" por exemplo
                e.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(err);
    }
    // Captura especificamente o erro de ID não encontrado e devolve 404
    @ExceptionHandler(EmpresaNaoEncontradaException.class)
    public ResponseEntity<StandardError> handleNaoEncontrado(EmpresaNaoEncontradaException e, HttpServletRequest request) {

        HttpStatus status = HttpStatus.NOT_FOUND;

        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                e.getClass().getSimpleName(),
                e.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<StandardError> handleDataIntegrityViolation(DataIntegrityViolationException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;

        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                "Violação de Integridade de Dados",
                "Não é possível excluir este registro, pois existem dados vinculados a ele (ex: notas fiscais).",
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        String mensagemErro = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();

        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                "Erro de Validação de Dados",
                mensagemErro,
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(err);
    }
}