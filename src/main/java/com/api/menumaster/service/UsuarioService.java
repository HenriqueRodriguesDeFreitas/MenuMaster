package com.api.menumaster.service;

import com.api.menumaster.dtos.request.RequestUsuarioDto;
import com.api.menumaster.dtos.response.ResponseUsuarioDto;
import com.api.menumaster.exception.custom.ConflictEntityException;
import com.api.menumaster.exception.custom.EntityNotFoundException;
import com.api.menumaster.model.Authority;
import com.api.menumaster.model.Usuario;
import com.api.menumaster.repository.AuthorityRepository;
import com.api.menumaster.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder encoder,
                          AuthorityRepository authorityRepository) {
        this.usuarioRepository = usuarioRepository;
        this.encoder = encoder;
        this.authorityRepository = authorityRepository;
    }

    @Transactional
    public ResponseUsuarioDto salvar(RequestUsuarioDto dto) {
        usuarioRepository.findByNome(dto.nome())
                .ifPresent(u -> {
                    throw new ConflictEntityException("Usuário já cadastrado");
                });

        Authority authority = authorityRepository.findByNome(dto.authority())
                .orElseThrow(() -> new EntityNotFoundException("Permissão não encontrada."));

        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setSenha(encoder.encode(dto.senha()));
        usuario.adicionarAuthority(authority);

        return converterUsuarioParaResponseDto(usuarioRepository.save(usuario));
    }

    private ResponseUsuarioDto converterUsuarioParaResponseDto(Usuario usuario) {
        Set<String> authorities = usuario.getAuthorities().stream()
                .map(a -> a.getAuthority().getNome()).collect(Collectors.toSet());
        return new ResponseUsuarioDto(usuario.getId(), usuario.getNome(), usuario.getSenha(), authorities);
    }
}
