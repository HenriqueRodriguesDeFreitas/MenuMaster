package com.api.menumaster.service.security;

import com.api.menumaster.configuration.UsuarioAutenticado;
import com.api.menumaster.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var usuario = usuarioRepository.findByNome(username)
                .orElseThrow(()-> new UsernameNotFoundException("Usuário não encontrado."));


        usuario.getAuthorities().size();
        return new UsuarioAutenticado(usuario);
    }
}
