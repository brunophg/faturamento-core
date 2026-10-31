
package com.faturamento.faturamento_core.api.controller;

import com.faturamento.faturamento_core.domain.repository.UsuarioRepository;
import com.faturamento.faturamento_core.domain.service.AuthService;
import com.faturamento.faturamento_core.domain.service.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//Spring carrega somente a estrutura necessária para testar a camada MVC, especialmente o controller indicado.
@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
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
}
