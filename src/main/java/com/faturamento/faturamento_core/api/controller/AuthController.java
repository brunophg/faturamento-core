package com.faturamento.faturamento_core.api.controller;


import com.faturamento.faturamento_core.domain.dto.auth.AuthRequestDTO;
import com.faturamento.faturamento_core.domain.dto.auth.RegisterRequestDTO;
import com.faturamento.faturamento_core.domain.dto.auth.TokenResponseDTO;
import com.faturamento.faturamento_core.domain.model.Usuario;
import com.faturamento.faturamento_core.domain.service.AuthService;
import com.faturamento.faturamento_core.domain.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final AuthService authService;


    public AuthController(AuthenticationManager authenticationManager, TokenService tokenService, AuthService authService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody @Valid AuthRequestDTO request) {
        UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(request.login(), request.senha());

        Authentication auth = this.authenticationManager.authenticate(usernamePassword);

        Usuario usuarioLogado = (Usuario) auth.getPrincipal();
        String tokenJwt = tokenService.gerarToken(usuarioLogado);

        return ResponseEntity.ok(new TokenResponseDTO(tokenJwt));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register (@RequestBody @Valid RegisterRequestDTO request) {

        if (!authService.cadastrar(request)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
