package com.api.menumaster.model;

import jakarta.persistence.*;
import org.springframework.security.core.CredentialsContainer;

import java.util.*;

@Entity
@Table(name = "usuario")
public class Usuario implements CredentialsContainer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 20, nullable = false, unique = true)
    private String nome;

    @Column(nullable = false)
    private String senha;

    @Column(name = "is_ativo", nullable = false)
    private boolean isAtivo;

    @OneToMany(mappedBy = "usuario", orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private Set<UsuarioAuthority> authorities = new HashSet<>();

    public Usuario() {
    }

    public Usuario(String nome, String senha) {
        this.nome = nome;
        this.senha = senha;
        this.isAtivo = true;
    }

    public Usuario(String nome, String senha, Set<UsuarioAuthority> usuarioAuthority) {
        this.nome = nome;
        this.senha = senha;
        this.authorities = usuarioAuthority;
        this.isAtivo = true;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public boolean isAtivo() {
        return isAtivo;
    }

    public void setAtivo(boolean ativo) {
        isAtivo = ativo;
    }

    public Set<UsuarioAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<UsuarioAuthority> authorities) {
        this.authorities = authorities;
    }

    public void adicionarAuthority(Authority authority) {
        UsuarioAuthority usuarioAuthority = new UsuarioAuthority(this, authority);
        this.authorities.add(usuarioAuthority);
    }

    @Override
    public void eraseCredentials() {
        this.senha = null;
    }
}
