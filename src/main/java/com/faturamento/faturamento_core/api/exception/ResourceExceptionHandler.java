package com.faturamento.faturamento_core.api.exception;

import com.faturamento.faturamento_core.domain.exception.RegraNegocioException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}