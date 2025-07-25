package com.api.menumaster.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 20, nullable = false)
    private String nome;

    @Column(length = 250)
    private String descricao;


    public Role(){}

    public Role(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
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

}
