package com.faturamento.faturamento_core.domain.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tb_usuario")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login", nullable = false, unique = true)
    private String login;

    @Column(name = "senha", nullable = false)
    private String senha;

    public Usuario() {
    }

    public Usuario(String login, String senha) {
        // Removido o this.id = id; pois o banco gera o ID automaticamente
        this.login = login;
        this.senha = senha;
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getSenha() {
        return senha;
    }

    // =========================================================
    // MÉTODOS DO USERDETAILS (O QUE O SPRING SECURITY LÊ)
    // =========================================================

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Por enquanto, todos têm o mesmo nível de acesso.
        // No futuro, se houver "ADMIN" e "VENDEDOR", a regra entra aqui.
        return List.of();
    }

    @Override
    public String getPassword() {
        return this.senha; // APONTAMENTO CORRETO PARA A SENHA
    }

    @Override
    public String getUsername() {
        return this.login; // APONTAMENTO CORRETO PARA O LOGIN
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Conta não expira
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Conta não está bloqueada
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Senha não expira
    }

    @Override
    public boolean isEnabled() {
        return true; // Usuário está ativo
    }

    // =========================================================
    // EQUALS & HASHCODE
    // =========================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}