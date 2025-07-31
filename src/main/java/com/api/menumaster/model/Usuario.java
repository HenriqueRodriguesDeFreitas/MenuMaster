package com.api.menumaster.model;

import jakarta.persistence.*;
import org.springframework.security.core.CredentialsContainer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    @OneToMany(mappedBy = "usuario", orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<UsuarioAuthority> authorities = new ArrayList<>();

    public Usuario() {
    }

    public Usuario(String nome, String senha) {
        this.nome = nome;
        this.senha = senha;
    }

    public Usuario(String nome, String senha, List<UsuarioAuthority> usuarioAuthority) {
        this.nome = nome;
        this.senha = senha;
        this.authorities = usuarioAuthority;
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


    public List<UsuarioAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<UsuarioAuthority> authorities) {
        this.authorities = authorities;
    }


    @Override
    public void eraseCredentials() {
        this.senha = null;
    }
}
