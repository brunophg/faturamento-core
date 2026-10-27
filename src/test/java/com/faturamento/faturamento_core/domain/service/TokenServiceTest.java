package com.faturamento.faturamento_core.domain.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.faturamento.faturamento_core.domain.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
class TokenServiceTest {

    private static final String SEGREDO = "segredo-de-teste";
    private static final String EMISSOR = "api-faturamento-core";

    private TokenService tokenService;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", SEGREDO);
        usuario = new Usuario("bruno", "senha-criptografada");
    }

    @Test
    @DisplayName("gerarToken: deve gerar um JWT com três partes (header.payload.assinatura)")
    void deveGerarTokenNoFormatoJwt() {
        String token = tokenService.gerarToken(usuario);

        assertNotNull(token);
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    @DisplayName("validarToken: um token gerado pelo serviço deve devolver o login do usuário")
    void deveDevolverLoginAoValidarTokenGerado() {
        String token = tokenService.gerarToken(usuario);

        assertEquals("bruno", tokenService.validarToken(token));
    }

    @Test
    @DisplayName("validarToken: deve devolver vazio para um texto que não é um JWT")
    void deveDevolverVazioParaTokenMalformado() {
        assertEquals("", tokenService.validarToken("abc"));
    }

    @Test
    @DisplayName("validarToken: deve devolver vazio quando a assinatura foi adulterada")
    void deveDevolverVazioParaTokenComAssinaturaAdulterada() {
        String token = tokenService.gerarToken(usuario);
        String adulterado = token.substring(0, token.lastIndexOf('.') + 1) + "assinaturaFalsa";

        assertEquals("", tokenService.validarToken(adulterado));
    }

    @Test
    @DisplayName("validarToken: deve devolver vazio para token assinado com outro segredo")
    void deveDevolverVazioParaTokenAssinadoComOutroSegredo() {
        TokenService outroServico = new TokenService();
        ReflectionTestUtils.setField(outroServico, "secret", "outro-segredo");
        String tokenDeOutroServico = outroServico.gerarToken(usuario);

        assertEquals("", tokenService.validarToken(tokenDeOutroServico));
    }

    @Test
    @DisplayName("validarToken: deve devolver vazio para token já expirado")
    void deveDevolverVazioParaTokenExpirado() {
        // Mesmo segredo e mesmo emissor: a única coisa errada é a data de expiração no passado
        String tokenExpirado = JWT.create()
                .withIssuer(EMISSOR)
                .withSubject("bruno")
                .withExpiresAt(Instant.now().minusSeconds(60))
                .sign(Algorithm.HMAC256(SEGREDO));

        assertEquals("", tokenService.validarToken(tokenExpirado));
    }

    @Test
    @DisplayName("validarToken: deve devolver vazio para token emitido por outro emissor")
    void deveDevolverVazioParaTokenDeOutroEmissor() {
        String tokenOutroEmissor = JWT.create()
                .withIssuer("outro-emissor")
                .withSubject("bruno")
                .withExpiresAt(Instant.now().plusSeconds(3600))
                .sign(Algorithm.HMAC256(SEGREDO));

        assertEquals("", tokenService.validarToken(tokenOutroEmissor));
    }

    @Test
    @DisplayName("gerarDataExpiracao: deve ser daqui a 2 horas, independente do fuso horário da máquina")
    void deveGerarExpiracaoDeDuasHoras() {
        Instant antes = Instant.now();
        Instant expiracao = tokenService.gerarDataExpiracao();
        Instant depois = Instant.now();

        Instant minimo = antes.plus(Duration.ofHours(2)).minusSeconds(5);
        Instant maximo = depois.plus(Duration.ofHours(2)).plusSeconds(5);

        assertFalse(expiracao.isBefore(minimo), "expiração antes do esperado: " + expiracao);
        assertFalse(expiracao.isAfter(maximo), "expiração depois do esperado: " + expiracao);
    }
}