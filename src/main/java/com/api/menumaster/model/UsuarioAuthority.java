package com.api.menumaster.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "usuario_authority")
public class UsuarioAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "authority_id")
    private Authority authority;

    public UsuarioAuthority(){}

    public UsuarioAuthority(Usuario usuario, Authority authority) {
        this.usuario = usuario;
        this.authority = authority;
    }

    public UUID getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Authority getAuthority() {
        return authority;
    }

    public void setAuthority(Authority authority) {
        this.authority = authority;
    }
}
