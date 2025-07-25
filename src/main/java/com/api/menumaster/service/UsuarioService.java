package com.api.menumaster.service;

import com.api.menumaster.dtos.request.RequestUsuarioDto;
import com.api.menumaster.dtos.response.ResponseUsuarioDto;
import com.api.menumaster.exception.custom.ConflictEntityException;
import com.api.menumaster.exception.custom.EntityNotFoundException;
import com.api.menumaster.model.Role;
import com.api.menumaster.model.Usuario;
import com.api.menumaster.repository.RoleRepository;
import com.api.menumaster.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository usuarioRepository, RoleRepository roleRepository,
                          PasswordEncoder encoder) {
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
    }

    @Transactional
    public ResponseUsuarioDto salvar(RequestUsuarioDto dto) {
        usuarioRepository.findByNome(dto.nome())
                .ifPresent(u -> {
                    throw new ConflictEntityException("Usuário já cadastrado");
                });

        Role role = roleRepository.findByNome(dto.role())
                .orElseThrow(() -> new EntityNotFoundException("Permissão não encontrada."));

        Usuario usuario = new Usuario(dto.nome(), encoder.encode(dto.senha()), role);

        return converterUsuarioParaResponseDto(usuarioRepository.save(usuario));
    }

    private ResponseUsuarioDto converterUsuarioParaResponseDto(Usuario usuario) {
        return new ResponseUsuarioDto(usuario.getId(), usuario.getNome(), usuario.getSenha(), usuario.getRole().getNome());
    }
}
