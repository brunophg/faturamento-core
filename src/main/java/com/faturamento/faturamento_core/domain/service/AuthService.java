package com.faturamento.faturamento_core.domain.service;

import com.faturamento.faturamento_core.domain.dto.auth.RegisterRequestDTO;
import com.faturamento.faturamento_core.domain.exception.LoginDuplicadoException;
import com.faturamento.faturamento_core.domain.model.Usuario;
import com.faturamento.faturamento_core.domain.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserDetails usuario = usuarioRepository.findByLogin(username);

        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario não encontrado: " + username);
        }

        return usuario;
    }

    public void cadastrar(RegisterRequestDTO request) {
        if (usuarioRepository.findByLogin(request.login()) != null) {
            throw new LoginDuplicadoException("Ja existe um usuario cadastrado com esse login");
        }
        String senhaCriptografada = passwordEncoder.encode(request.senha());


        Usuario novoUsuario = new Usuario(request.login(), senhaCriptografada);
        usuarioRepository.save(novoUsuario);
    }

}
