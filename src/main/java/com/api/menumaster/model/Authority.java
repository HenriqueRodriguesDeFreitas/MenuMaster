package com.api.menumaster.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "authority")
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 50, nullable = false, unique = true)
    private String nome;

    @Column(length = 200)
    private String descricao;

    @OneToMany(mappedBy = "authority")
    private List<UsuarioAuthority> usuarioAuthority;

    public Authority(){}

    public Authority(String nome, String descricao, List<UsuarioAuthority> usuarioAuthority) {
        this.nome = nome;
        this.descricao = descricao;
        this.usuarioAuthority = usuarioAuthority;
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<UsuarioAuthority> getUsuarioAuthority() {
        return usuarioAuthority;
    }

    public void setUsuarioAuthority(List<UsuarioAuthority> usuarioAuthority) {
        this.usuarioAuthority = usuarioAuthority;
    }
}
