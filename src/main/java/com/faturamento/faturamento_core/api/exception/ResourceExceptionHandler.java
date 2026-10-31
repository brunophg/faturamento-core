package com.faturamento.faturamento_core.api.exception;

import com.faturamento.faturamento_core.domain.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ResourceExceptionHandler {

    //CnpjInvalido
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

    @ExceptionHandler({CnpjDuplicadoException.class, NotaFiscalDuplicadaException.class, LoginDuplicadoException.class})
    public ResponseEntity<StandardError> handleDuplicidade(RegraNegocioException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;

        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                e.getClass().getSimpleName(),
                e.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(err);
    }

    // Captura especificamente o erro de ID não encontrado e devolve 404
    @ExceptionHandler(EmpresaNaoEncontradaException.class)
    public ResponseEntity<StandardError> handleEmpresaNaoEncontrada(EmpresaNaoEncontradaException e, HttpServletRequest request) {

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
    public ResponseEntity<StandardError> handleDataIntegrityViolation(
            DataIntegrityViolationException e,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.CONFLICT;

        boolean loginDuplicado = isLoginUniqueViolation(e);

        String titulo = loginDuplicado
                ? "Login duplicado"
                : "Violação de Integridade de Dados";

        String mensagem = loginDuplicado
                ? "Já existe um usuário cadastrado com esse login."
                : "Não foi possível concluir a operação devido a uma "
                + "violação de integridade dos dados.";

        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                titulo,
                mensagem,
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(err);
    }

    private boolean isLoginUniqueViolation(Throwable exception) {
        Throwable causa = exception;

        while (causa != null) {
            if (causa instanceof
                    org.hibernate.exception.ConstraintViolationException violacao) {

                if ("uk_usuario_login".equalsIgnoreCase(
                        violacao.getConstraintName())) {
                    return true;
                }
            }

            causa = causa.getCause();
        }

        return false;
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

    @ExceptionHandler(NotaFiscalNaoEncontradaException.class)
    public ResponseEntity<StandardError> handleNotaNaoEncontrada(NotaFiscalNaoEncontradaException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError err = new StandardError(Instant.now(), status.value(), e.getClass().getSimpleName(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(ProdutoNaoEncontradoException.class)
    public ResponseEntity<StandardError> handleProdutoNaoEncontrado(ProdutoNaoEncontradoException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError err = new StandardError(Instant.now(), status.value(), e.getClass().getSimpleName(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(ProdutoDuplicadoException.class)
    public ResponseEntity<StandardError> handleProdutoDuplicado(ProdutoDuplicadoException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        StandardError err = new StandardError(Instant.now(), status.value(), e.getClass().getSimpleName(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(StatusNotaInvalidoException.class)
    public ResponseEntity<StandardError> handleStatusNotaInvalido(StatusNotaInvalidoException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        StandardError err = new StandardError(Instant.now(), status.value(), e.getClass().getSimpleName(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<StandardError> handleBadCredentials(
            BadCredentialsException e,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.UNAUTHORIZED;

        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                "Credenciais inválidas",
                "Usuário ou senha inválidos.",
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(err);
    }
}