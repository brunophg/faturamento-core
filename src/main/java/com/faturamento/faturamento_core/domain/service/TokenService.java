package com.faturamento.faturamento_core.domain.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.faturamento.faturamento_core.domain.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String gerarToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.create()
                    .withIssuer("api-faturamento-core") // quem emitiu
                    .withSubject(usuario.getLogin())
                    .withExpiresAt(gerarDataExpiracao())
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            throw new RuntimeException("Erro ao gerar o token JWT", e);
        }
    }

    public Optional<String> validarToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            String login = JWT.require(algorithm)
                    .withIssuer("api-faturamento-core")
                    .build()
                    .verify(token)
                    .getSubject();

            if (login == null || login.isBlank()) {
                return Optional.empty();
            }

            return Optional.of(login);

        } catch (JWTVerificationException e) {
            return Optional.empty();
        }
    }

    // metodo para definir 2 horas de validade para o token
    public Instant gerarDataExpiracao() {
        return Instant.now().plus(Duration.ofHours(2));
    }
}
