package com.faturamento.faturamento_core.api.controller;


import com.faturamento.faturamento_core.domain.dto.auth.AuthRequestDTO;
import com.faturamento.faturamento_core.domain.dto.auth.RegisterRequestDTO;
import com.faturamento.faturamento_core.domain.dto.auth.TokenResponseDTO;
import com.faturamento.faturamento_core.domain.model.Usuario;
import com.faturamento.faturamento_core.domain.repository.UsuarioRepository;
import com.faturamento.faturamento_core.domain.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, TokenService tokenService, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
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
        if (usuarioRepository.findByLogin(request.login()) != null) {
            return ResponseEntity.badRequest().build();
        }

        String senhaCriptografada = passwordEncoder.encode(request.senha());

        Usuario novoUsuario = new Usuario(request.login(), senhaCriptografada);
        usuarioRepository.save(novoUsuario);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
