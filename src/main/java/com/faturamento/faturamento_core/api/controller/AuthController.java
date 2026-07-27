package com.faturamento.faturamento_core.api.controller;


import com.faturamento.faturamento_core.domain.dto.auth.AuthRequestDTO;
import com.faturamento.faturamento_core.domain.dto.auth.TokenResponseDTO;
import com.faturamento.faturamento_core.domain.model.Usuario;
import com.faturamento.faturamento_core.domain.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/login")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthController(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping
    public ResponseEntity<TokenResponseDTO> login(@RequestBody @Valid AuthRequestDTO request) {
        UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(request.login(), request.senha());

        Authentication auth = this.authenticationManager.authenticate(usernamePassword);

        Usuario usuarioLogado = (Usuario) auth.getPrincipal();

        String tokenJwt = tokenService.gerarToken(usuarioLogado);

        return ResponseEntity.ok(new TokenResponseDTO(tokenJwt));
    }
}
