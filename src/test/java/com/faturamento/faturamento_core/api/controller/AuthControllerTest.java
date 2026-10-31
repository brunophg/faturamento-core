
package com.faturamento.faturamento_core.api.controller;

import com.faturamento.faturamento_core.api.exception.ResourceExceptionHandler;
import com.faturamento.faturamento_core.domain.exception.LoginDuplicadoException;
import com.faturamento.faturamento_core.domain.model.Usuario;
import com.faturamento.faturamento_core.domain.repository.UsuarioRepository;
import com.faturamento.faturamento_core.domain.service.AuthService;
import com.faturamento.faturamento_core.domain.service.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//Spring carrega somente a estrutura necessária para testar a camada MVC, especialmente o controller indicado.
@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ResourceExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("Deve cadastrar usuario com sucesso")
    void deveCadastrarUsuarioComSucesso() throws Exception {

        String json = """
                {
                    "login": "admin",
                    "senha": "admin123"
                }
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated());

        verify(authService, times(1)).cadastrar(any());
    }

    @Test
    @DisplayName("Deve retornar 400 quando o login estiver vazio")
    void deveRetornarBadRequestQuandoLoginVazio() throws Exception {

        String json = """
                
                { 
                    "login": "",
                    "senha": "admin123"
                }
            
                """;

        mockMvc.perform(post("/auth/register")
                    .contentType("application/json")
                    .content(json))
                .andExpect(status().isBadRequest());

        verify(authService, never())
                .cadastrar(any());
    }

    @Test
    @DisplayName("Deve retornar 409 quando login duplicado")
    void deveRetornarConflictQuandoLoginDuplicado() throws Exception{
        String json = """
                
                {
                    "login": "admin",
                    "senha": "admin123"
                }
                """;

        doThrow(new LoginDuplicadoException(
                "Ja existe um usuario cadastrado com esse login"))
                .when(authService)
                .cadastrar(any());

        mockMvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .content(json))
                .andDo(print())
                .andExpect(status().isConflict());

        verify(authService, times(1))
                .cadastrar(any());

    }

    @Test
    @DisplayName("Deve realizar login com sucesso")
    void deveRealizarLoginComSucesso() throws Exception {
        String json = """
                
                {
                    "login": "admin",
                    "senha": "admin123"
                }
                """;
        Usuario usuario = new Usuario("admin", "senhaCriptografada");

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                usuario,
                null,
                usuario.getAuthorities()
        );

        when(authenticationManager.authenticate(any(
                UsernamePasswordAuthenticationToken.class
        ))).thenReturn(authentication);

        when(tokenService.gerarToken(usuario))
                .thenReturn("token-jwt-teste");

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-jwt-teste"));

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("Deve retornar 401 quando as credenciais forem invalidas")
    void deveRetornarUnauthorizedQuandoCredenciaisInvalidas() throws Exception{
        String json = """
                 {
                    "login": "admin",
                    "senha": "senhaErrada"
                }
                """;

        when(authenticationManager.authenticate(any(
                UsernamePasswordAuthenticationToken.class
        ))).thenThrow(new BadCredentialsException("Credenciais invalidas"));

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(json))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        verify(tokenService, never()).gerarToken(any());
    }
    }
