package com.faturamento.faturamento_core.domain.service;

import com.faturamento.faturamento_core.domain.dto.auth.RegisterRequestDTO;
import com.faturamento.faturamento_core.domain.exception.LoginDuplicadoException;
import com.faturamento.faturamento_core.domain.model.Usuario;
import com.faturamento.faturamento_core.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private AuthService authService;

    @Mock
    private PasswordEncoder passwordEncoder;

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

    @Test
    @DisplayName("cadastrar: deve salvar usuário com senha criptografada")
    void deveCadastrarUsuarioComSenhaCriptografada() {
        String login = "novoUsuario";
        String senha = "senha123";
        String senhaCriptografada = "hash-da-senha";

        RegisterRequestDTO request =
                new RegisterRequestDTO(login, senha);

        when(usuarioRepository.findByLogin(login)).thenReturn(null);
        when(passwordEncoder.encode(senha))
                .thenReturn(senhaCriptografada);

        assertDoesNotThrow(() -> authService.cadastrar(request));

        ArgumentCaptor<Usuario> captor =
                ArgumentCaptor.forClass(Usuario.class);

        verify(usuarioRepository).save(captor.capture());

        Usuario usuarioSalvo = captor.getValue();

        assertEquals(login, usuarioSalvo.getUsername());
        assertEquals(senhaCriptografada, usuarioSalvo.getPassword());

        verify(passwordEncoder).encode(senha);
    }


    @Test
    @DisplayName("cadastrar: deve lançar exceção quando login já existe")
    void deveLancarExcecaoQuandoLoginJaExiste() {
        String login = "bruno";

        RegisterRequestDTO request =
                new RegisterRequestDTO(login, "senha123");

        when(usuarioRepository.findByLogin(login))
                .thenReturn(new Usuario(login, "hash-existente"));

        assertThrows(
                LoginDuplicadoException.class,
                () -> authService.cadastrar(request)
        );

        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

}