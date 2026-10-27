package com.faturamento.faturamento_core.domain.service;

import com.faturamento.faturamento_core.domain.model.Usuario;
import com.faturamento.faturamento_core.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("loadUserByUsername: deve devolver o usuário quando o login existe")
    void deveDevolverUsuarioQuandoLoginExiste() {
        Usuario usuario = new Usuario("bruno", "senha-criptografada");

        when(usuarioRepository.findByLogin("bruno")).thenReturn(usuario);

        UserDetails resultado = authService.loadUserByUsername("bruno");

        assertEquals("bruno", resultado.getUsername());
        assertEquals("senha-criptografada", resultado.getPassword());
    }

    @Test
    @DisplayName("loadUserByUsername: deve lançar UsernameNotFoundException quando o login não existe")
    void deveLancarExcecaoQuandoLoginNaoExiste() {
        // O repositório devolve null (e não Optional) quando não encontra o login
        when(usuarioRepository.findByLogin("inexistente")).thenReturn(null);

        UsernameNotFoundException excecao = assertThrows(UsernameNotFoundException.class,
                () -> authService.loadUserByUsername("inexistente"));

        assertTrue(excecao.getMessage().contains("inexistente"));
    }
}