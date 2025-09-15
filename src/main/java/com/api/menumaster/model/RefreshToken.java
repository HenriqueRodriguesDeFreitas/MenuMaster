package com.api.menumaster.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_token")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "jwt_id", nullable = false)
    private String jwtId;

    @Column(name = "usuario", nullable = false)
    private String usuario;

    @Column(name = "expiracao", nullable = false)
    private Instant expiracao;

    @Column(name = "revogado")
    private boolean revogado = false;

    @Column(name = "substituido_por")
    private String substituidoPor;

    @Column(name = "criado_em")
    private Instant criadoEm = Instant.now();

    public RefreshToken() {
    }

    public RefreshToken(String jwtId, String usuario, Instant expiracao, boolean revogado, String substituidoPor, Instant criadoEm) {
        this.jwtId = jwtId;
        this.usuario = usuario;
        this.expiracao = expiracao;
        this.revogado = revogado;
        this.substituidoPor = substituidoPor;
        this.criadoEm = criadoEm;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getJwtId() {
        return jwtId;
    }

    public void setJwtId(String jwtId) {
        this.jwtId = jwtId;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Instant getExpiracao() {
        return expiracao;
    }

    public void setExpiracao(Instant expiracao) {
        this.expiracao = expiracao;
    }

    public boolean isRevogado() {
        return revogado;
    }

    public void setRevogado(boolean revogado) {
        this.revogado = revogado;
    }

    public String getSubstituidoPor() {
        return substituidoPor;
    }

    public void setSubstituidoPor(String substituidoPor) {
        this.substituidoPor = substituidoPor;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Instant criadoEm) {
        this.criadoEm = criadoEm;
    }
}
