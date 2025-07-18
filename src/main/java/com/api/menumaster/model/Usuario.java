package com.api.menumaster.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 20, nullable = false, unique = true)
    private String nome;

    @Column(nullable = false)
    private String senha;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    public Usuario(){}

    public Usuario(String nome, String senha, Role role) {
        this.nome = nome;
        this.senha = senha;
        this.role = role;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
