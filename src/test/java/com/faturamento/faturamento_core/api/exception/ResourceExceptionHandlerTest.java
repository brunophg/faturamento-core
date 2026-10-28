
package com.faturamento.faturamento_core.api.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ResourceExceptionHandlerTest {

    private ResourceExceptionHandler exceptionHandler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        exceptionHandler = new ResourceExceptionHandler();
        request = mock(HttpServletRequest.class);

        when(request.getRequestURI()).thenReturn("/auth/register");
    }

    @Test
    @DisplayName("Deve retornar 409 específico quando o login for duplicado")
    void deveRetornarConflitoQuandoLoginForDuplicado() {
        ConstraintViolationException violacao =
                new ConstraintViolationException(
                        "Unique constraint violation",
                        new SQLException("Duplicate key"),
                        "uk_usuario_login"
                );

        DataIntegrityViolationException excecao =
                new DataIntegrityViolationException(
                        "Erro de integridade",
                        violacao
                );

        ResponseEntity<StandardError> response =
                exceptionHandler.handleDataIntegrityViolation(
                        excecao,
                        request
                );

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().getStatus());
        assertEquals("Login duplicado", response.getBody().getError());
        assertEquals(
                "Já existe um usuário cadastrado com esse login.",
                response.getBody().getMessage()
        );
        assertEquals("/auth/register", response.getBody().getPath());
    }

    @Test
    @DisplayName("Deve retornar mensagem genérica para outras violações")
    void deveRetornarMensagemGenericaParaOutraViolacao() {
        DataIntegrityViolationException excecao =
                new DataIntegrityViolationException(
                        "Outra violação de integridade"
                );

        ResponseEntity<StandardError> response =
                exceptionHandler.handleDataIntegrityViolation(
                        excecao,
                        request
                );

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().getStatus());
        assertEquals(
                "Violação de Integridade de Dados",
                response.getBody().getError()
        );
        assertEquals(
                "Não foi possível concluir a operação devido a uma "
                        + "violação de integridade dos dados.",
                response.getBody().getMessage()
        );
        assertEquals("/auth/register", response.getBody().getPath());
    }
}
