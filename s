[1mdiff --git a/src/main/java/com/faturamento/faturamento_core/api/controller/AuthController.java b/src/main/java/com/faturamento/faturamento_core/api/controller/AuthController.java[m
[1mindex d7e2f68..199ff98 100644[m
[1m--- a/src/main/java/com/faturamento/faturamento_core/api/controller/AuthController.java[m
[1m+++ b/src/main/java/com/faturamento/faturamento_core/api/controller/AuthController.java[m
[36m@@ -47,9 +47,7 @@[m [mpublic class AuthController {[m
     @PostMapping("/register")[m
     public ResponseEntity<Void> register (@RequestBody @Valid RegisterRequestDTO request) {[m
 [m
[31m-        if (!authService.cadastrar(request)) {[m
[31m-            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();[m
[31m-        }[m
[32m+[m[32m        authService.cadastrar(request);[m
 [m
         return ResponseEntity.status(HttpStatus.CREATED).build();[m
     }[m
[1mdiff --git a/src/main/java/com/faturamento/faturamento_core/api/exception/ResourceExceptionHandler.java b/src/main/java/com/faturamento/faturamento_core/api/exception/ResourceExceptionHandler.java[m
[1mindex f175cc0..cbcf830 100644[m
[1m--- a/src/main/java/com/faturamento/faturamento_core/api/exception/ResourceExceptionHandler.java[m
[1m+++ b/src/main/java/com/faturamento/faturamento_core/api/exception/ResourceExceptionHandler.java[m
[36m@@ -31,7 +31,7 @@[m [mpublic class ResourceExceptionHandler {[m
         return ResponseEntity.status(status).body(err);[m
     }[m
 [m
[31m-    @ExceptionHandler({CnpjDuplicadoException.class, NotaFiscalDuplicadaException.class})[m
[32m+[m[32m    @ExceptionHandler({CnpjDuplicadoException.class, NotaFiscalDuplicadaException.class, LoginDuplicadoException.class})[m
     public ResponseEntity<StandardError> handleDuplicidade(RegraNegocioException e, HttpServletRequest request) {[m
         HttpStatus status = HttpStatus.CONFLICT;[m
 [m
[36m@@ -64,20 +64,53 @@[m [mpublic class ResourceExceptionHandler {[m
     }[m
 [m
     @ExceptionHandler(DataIntegrityViolationException.class)[m
[31m-    public ResponseEntity<StandardError> handleDataIntegrityViolation(DataIntegrityViolationException e, HttpServletRequest request) {[m
[32m+[m[32m    public ResponseEntity<StandardError> handleDataIntegrityViolation([m
[32m+[m[32m            DataIntegrityViolationException e,[m
[32m+[m[32m            HttpServletRequest request) {[m
[32m+[m
         HttpStatus status = HttpStatus.CONFLICT;[m
 [m
[32m+[m[32m        boolean loginDuplicado = isLoginUniqueViolation(e);[m
[32m+[m
[32m+[m[32m        String titulo = loginDuplicado[m
[32m+[m[32m                ? "Login duplicado"[m
[32m+[m[32m                : "Violação de Integridade de Dados";[m
[32m+[m
[32m+[m[32m        String mensagem = loginDuplicado[m
[32m+[m[32m                ? "Já existe um usuário cadastrado com esse login."[m
[32m+[m[32m                : "Não foi possível concluir a operação devido a uma "[m
[32m+[m[32m                + "violação de integridade dos dados.";[m
[32m+[m
         StandardError err = new StandardError([m
                 Instant.now(),[m
                 status.value(),[m
[31m-                "Violação de Integridade de Dados",[m
[31m-                "Não é possível excluir este registro, pois existem dados vinculados a ele (ex: notas fiscais).",[m
[32m+[m[32m                titulo,[m
[32m+[m[32m                mensagem,[m
                 request.getRequestURI()[m
         );[m
 [m
         return ResponseEntity.status(status).body(err);[m
     }[m
 [m
[32m+[m[32m    private boolean isLoginUniqueViolation(Throwable exception) {[m
[32m+[m[32m        Throwable causa = exception;[m
[32m+[m
[32m+[m[32m        while (causa != null) {[m
[32m+[m[32m            if (causa instanceof[m
[32m+[m[32m                    org.hibernate.exception.ConstraintViolationException violacao) {[m
[32m+[m
[32m+[m[32m                if ("uk_usuario_login".equalsIgnoreCase([m
[32m+[m[32m                        violacao.getConstraintName())) {[m
[32m+[m[32m                    return true;[m
[32m+[m[32m                }[m
[32m+[m[32m            }[m
[32m+[m
[32m+[m[32m            causa = causa.getCause();[m
[32m+[m[32m        }[m
[32m+[m
[32m+[m[32m        return false;[m
[32m+[m[32m    }[m
[32m+[m
     @ExceptionHandler(MethodArgumentNotValidException.class)[m
     public ResponseEntity<StandardError> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request) {[m
         HttpStatus status = HttpStatus.BAD_REQUEST;[m
[1mdiff --git a/src/main/java/com/faturamento/faturamento_core/domain/exception/LoginDuplicadoException.java b/src/main/java/com/faturamento/faturamento_core/domain/exception/LoginDuplicadoException.java[m
[1mnew file mode 100644[m
[1mindex 0000000..28ab3c7[m
[1m--- /dev/null[m
[1m+++ b/src/main/java/com/faturamento/faturamento_core/domain/exception/LoginDuplicadoException.java[m
[36m@@ -0,0 +1,7 @@[m
[32m+[m[32mpackage com.faturamento.faturamento_core.domain.exception;[m
[32m+[m
[32m+[m[32mpublic class LoginDuplicadoException extends RegraNegocioException {[m
[32m+[m[32m    public LoginDuplicadoException(String message) {[m
[32m+[m[32m        super(message);[m
[32m+[m[32m    }[m
[32m+[m[32m}[m
[1mdiff --git a/src/main/java/com/faturamento/faturamento_core/domain/model/Usuario.java b/src/main/java/com/faturamento/faturamento_core/domain/model/Usuario.java[m
[1mindex 492b72b..1c94f81 100644[m
[1m--- a/src/main/java/com/faturamento/faturamento_core/domain/model/Usuario.java[m
[1m+++ b/src/main/java/com/faturamento/faturamento_core/domain/model/Usuario.java[m
[36m@@ -9,14 +9,20 @@[m [mimport java.util.List;[m
 import java.util.Objects;[m
 [m
 @Entity[m
[31m-@Table(name = "tb_usuario")[m
[32m+[m[32m@Table(name = "tb_usuario",[m
[32m+[m[32muniqueConstraints = {[m
[32m+[m[32m        @UniqueConstraint([m
[32m+[m[32m                name = "uk_usuario_login",[m
[32m+[m[32m                columnNames = "login"[m
[32m+[m[32m        )[m
[32m+[m[32m})[m
 public class Usuario implements UserDetails {[m
 [m
     @Id[m
     @GeneratedValue(strategy = GenerationType.IDENTITY)[m
     private Long id;[m
 [m
[31m-    @Column(name = "login", nullable = false, unique = true)[m
[32m+[m[32m    @Column(name = "login", nullable = false)[m
     private String login;[m
 [m
     @Column(name = "senha", nullable = false)[m
[1mdiff --git a/src/main/java/com/faturamento/faturamento_core/domain/service/AuthService.java b/src/main/java/com/faturamento/faturamento_core/domain/service/AuthService.java[m
[1mindex 9b51c9b..11a736c 100644[m
[1m--- a/src/main/java/com/faturamento/faturamento_core/domain/service/AuthService.java[m
[1m+++ b/src/main/java/com/faturamento/faturamento_core/domain/service/AuthService.java[m
[36m@@ -1,6 +1,7 @@[m
 package com.faturamento.faturamento_core.domain.service;[m
 [m
 import com.faturamento.faturamento_core.domain.dto.auth.RegisterRequestDTO;[m
[32m+[m[32mimport com.faturamento.faturamento_core.domain.exception.LoginDuplicadoException;[m
 import com.faturamento.faturamento_core.domain.model.Usuario;[m
 import com.faturamento.faturamento_core.domain.repository.UsuarioRepository;[m
 import org.springframework.security.core.userdetails.UserDetails;[m
[36m@@ -33,17 +34,15 @@[m [mpublic class AuthService implements UserDetailsService {[m
         return usuario;[m
     }[m
 [m
[31m-    public boolean cadastrar(RegisterRequestDTO request) {[m
[32m+[m[32m    public void cadastrar(RegisterRequestDTO request) {[m
         if (usuarioRepository.findByLogin(request.login()) != null) {[m
[31m-            return false;[m
[32m+[m[32m            throw new LoginDuplicadoException("Ja existe um usuario cadastrado com esse login");[m
         }[m
         String senhaCriptografada = passwordEncoder.encode(request.senha());[m
 [m
 [m
         Usuario novoUsuario = new Usuario(request.login(), senhaCriptografada);[m
         usuarioRepository.save(novoUsuario);[m
[31m-[m
[31m-        return true;[m
     }[m
 [m
 }[m
[1mdiff --git a/src/test/java/com/faturamento/faturamento_core/api/exception/ResourceExceptionHandlerTest.java b/src/test/java/com/faturamento/faturamento_core/api/exception/ResourceExceptionHandlerTest.java[m
[1mnew file mode 100644[m
[1mindex 0000000..71ae01d[m
[1m--- /dev/null[m
[1m+++ b/src/test/java/com/faturamento/faturamento_core/api/exception/ResourceExceptionHandlerTest.java[m
[36m@@ -0,0 +1,92 @@[m
[32m+[m
[32m+[m[32mpackage com.faturamento.faturamento_core.api.exception;[m
[32m+[m
[32m+[m[32mimport jakarta.servlet.http.HttpServletRequest;[m
[32m+[m[32mimport org.hibernate.exception.ConstraintViolationException;[m
[32m+[m[32mimport org.junit.jupiter.api.BeforeEach;[m
[32m+[m[32mimport org.junit.jupiter.api.DisplayName;[m
[32m+[m[32mimport org.junit.jupiter.api.Test;[m
[32m+[m[32mimport org.springframework.dao.DataIntegrityViolationException;[m
[32m+[m[32mimport org.springframework.http.HttpStatus;[m
[32m+[m[32mimport org.springframework.http.ResponseEntity;[m
[32m+[m
[32m+[m[32mimport java.sql.SQLException;[m
[32m+[m
[32m+[m[32mimport static org.junit.jupiter.api.Assertions.*;[m
[32m+[m[32mimport static org.mockito.Mockito.*;[m
[32m+[m
[32m+[m[32mclass ResourceExceptionHandlerTest {[m
[32m+[m
[32m+[m[32m    private ResourceExceptionHandler exceptionHandler;[m
[32m+[m[32m    private HttpServletRequest request;[m
[32m+[m
[32m+[m[32m    @BeforeEach[m
[32m+[m[32m    void setUp() {[m
[32m+[m[32m        exceptionHandler = new ResourceExceptionHandler();[m
[32m+[m[32m        request = mock(HttpServletRequest.class);[m
[32m+[m
[32m+[m[32m        when(request.getRequestURI()).thenReturn("/auth/register");[m
[32m+[m[32m    }[m
[32m+[m
[32m+[m[32m    @Test[m
[32m+[m[32m    @DisplayName("Deve retornar 409 específico quando o login for duplicado")[m
[32m+[m[32m    void deveRetornarConflitoQuandoLoginForDuplicado() {[m
[32m+[m[32m        ConstraintViolationException violacao =[m
[32m+[m[32m                new ConstraintViolationException([m
[32m+[m[32m                        "Unique constraint violation",[m
[32m+[m[32m                        new SQLException("Duplicate key"),[m
[32m+[m[32m                        "uk_usuario_login"[m
[32m+[m[32m                );[m
[32m+[m
[32m+[m[32m        DataIntegrityViolationException excecao =[m
[32m+[m[32m                new DataIntegrityViolationException([m
[32m+[m[32m                        "Erro de integridade",[m
[32m+[m[32m                        violacao[m
[32m+[m[32m                );[m
[32m+[m
[32m+[m[32m        ResponseEntity<StandardError> response =[m
[32m+[m[32m                exceptionHandler.handleDataIntegrityViolation([m
[32m+[m[32m                        excecao,[m
[32m+[m[32m                        request[m
[32m+[m[32m                );[m
[32m+[m
[32m+[m[32m        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());[m
[32m+[m[32m        assertNotNull(response.getBody());[m
[32m+[m[32m        assertEquals(409, response.getBody().getStatus());[m
[32m+[m[32m        assertEquals("Login duplicado", response.getBody().getError());[m
[32m+[m[32m        assertEquals([m
[32m+[m[32m                "Já existe um usuário cadastrado com esse login.",[m
[32m+[m[32m                response.getBody().getMessage()[m
[32m+[m[32m        );[m
[32m+[m[32m        assertEquals("/auth/register", response.getBody().getPath());[m
[32m+[m[32m    }[m
[32m+[m
[32m+[m[32m    @Test[m
[32m+[m[32m    @DisplayName("Deve retornar mensagem genérica para outras violações")[m
[32m+[m[32m    void deveRetornarMensagemGenericaParaOutraViolacao() {[m
[32m+[m[32m        DataIntegrityViolationException excecao =[m
[32m+[m[32m                new DataIntegrityViolationException([m
[32m+[m[32m                        "Outra violação de integridade"[m
[32m+[m[32m                );[m
[32m+[m
[32m+[m[32m        ResponseEntity<StandardError> response =[m
[32m+[m[32m                exceptionHandler.handleDataIntegrityViolation([m
[32m+[m[32m                        excecao,[m
[32m+[m[32m                        request[m
[32m+[m[32m                );[m
[32m+[m
[32m+[m[32m        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());[m
[32m+[m[32m        assertNotNull(response.getBody());[m
[32m+[m[32m        assertEquals(409, response.getBody().getStatus());[m
[32m+[m[32m        assertEquals([m
[32m+[m[32m                "Violação de Integridade de Dados",[m
[32m+[m[32m                response.getBody().getError()[m
[32m+[m[32m        );[m
[32m+[m[32m        assertEquals([m
[32m+[m[32m                "Não foi possível concluir a operação devido a uma "[m
[32m+[m[32m                        + "violação de integridade dos dados.",[m
[32m+[m[32m                response.getBody().getMessage()[m
[32m+[m[32m        );[m
[32m+[m[32m        assertEquals("/auth/register", response.getBody().getPath());[m
[32m+[m[32m    }[m
[32m+[m[32m}[m
[1mdiff --git a/src/test/java/com/faturamento/faturamento_core/domain/service/AuthServiceTest.java b/src/test/java/com/faturamento/faturamento_core/domain/service/AuthServiceTest.java[m
[1mindex df0516c..2a9184f 100644[m
[1m--- a/src/test/java/com/faturamento/faturamento_core/domain/service/AuthServiceTest.java[m
[1m+++ b/src/test/java/com/faturamento/faturamento_core/domain/service/AuthServiceTest.java[m
[36m@@ -1,6 +1,7 @@[m
 package com.faturamento.faturamento_core.domain.service;[m
 [m
 import com.faturamento.faturamento_core.domain.dto.auth.RegisterRequestDTO;[m
[32m+[m[32mimport com.faturamento.faturamento_core.domain.exception.LoginDuplicadoException;[m
 import com.faturamento.faturamento_core.domain.model.Usuario;[m
 import com.faturamento.faturamento_core.domain.repository.UsuarioRepository;[m
 import org.junit.jupiter.api.DisplayName;[m
[36m@@ -68,9 +69,7 @@[m [mclass AuthServiceTest {[m
         when(passwordEncoder.encode(senha))[m
                 .thenReturn(senhaCriptografada);[m
 [m
[31m-        boolean resultado = authService.cadastrar(request);[m
[31m-[m
[31m-        assertTrue(resultado);[m
[32m+[m[32m        assertDoesNotThrow(() -> authService.cadastrar(request));[m
 [m
         ArgumentCaptor<Usuario> captor =[m
                 ArgumentCaptor.forClass(Usuario.class);[m
[36m@@ -85,26 +84,25 @@[m [mclass AuthServiceTest {[m
         verify(passwordEncoder).encode(senha);[m
     }[m
 [m
[32m+[m
     @Test[m
[31m-    @DisplayName("cadastrar: deve recusar login já existente")[m
[31m-    void deveRecusarCadastroQuandoLoginJaExiste() {[m
[32m+[m[32m    @DisplayName("cadastrar: deve lançar exceção quando login já existe")[m
[32m+[m[32m    void deveLancarExcecaoQuandoLoginJaExiste() {[m
         String login = "bruno";[m
[31m-        String senha = "senha123";[m
 [m
         RegisterRequestDTO request =[m
[31m-                new RegisterRequestDTO(login, senha);[m
[31m-[m
[31m-        Usuario usuarioExistente =[m
[31m-                new Usuario(login, "hash-existente");[m
[32m+[m[32m                new RegisterRequestDTO(login, "senha123");[m
 [m
         when(usuarioRepository.findByLogin(login))[m
[31m-                .thenReturn(usuarioExistente);[m
[31m-[m
[31m-        boolean resultado = authService.cadastrar(request);[m
[32m+[m[32m                .thenReturn(new Usuario(login, "hash-existente"));[m
 [m
[31m-        assertFalse(resultado);[m
[32m+[m[32m        assertThrows([m
[32m+[m[32m                LoginDuplicadoException.class,[m
[32m+[m[32m                () -> authService.cadastrar(request)[m
[32m+[m[32m        );[m
 [m
         verify(usuarioRepository, never()).save(any(Usuario.class));[m
         verify(passwordEncoder, never()).encode(anyString());[m
     }[m
[32m+[m
 }[m
\ No newline at end of file[m
