package com.api.menumaster.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "fornecedor")
public class Fornecedor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "razao_social", unique = true, nullable = false)
    private String razaoSocial;

    @Column(name = "nome_fantasia", unique = true, nullable = false)
    private String nomeFantasia;

    @Column(name = "cnpj", length = 14, nullable = false, unique = true)
    private String cnpj;

    @Column(name = "inscricao_estadual", length = 9, nullable = false, unique = true)
    private String inscricaoEstadual;

    @Column(name = "endereco")
    private String endereco;

    @Column
    private String contato;

    @Column(name = "is_ativo")
    private boolean isAtivo;

    public Fornecedor() {
    }

    public Fornecedor(String razaoSocial, String nomeFantasia, String cnpj, String inscricaoEstadual, String endereco, String contato, boolean isAtivo) {
        this.razaoSocial = razaoSocial;
        this.nomeFantasia = nomeFantasia;
        this.cnpj = cnpj;
        this.inscricaoEstadual = inscricaoEstadual;
        this.endereco = endereco;
        this.contato = contato;
        this.isAtivo = isAtivo;
    }

    public UUID getId() {
        return id;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getInscricaoEstadual() {
        return inscricaoEstadual;
    }

    public void setInscricaoEstadual(String inscricaoEstadual) {
        this.inscricaoEstadual = inscricaoEstadual;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public boolean isAtivo() {
        return isAtivo;
    }

    public void setAtivo(boolean ativo) {
        isAtivo = ativo;
    }
}
