package com.api.menumaster.service;

import com.api.menumaster.dtos.request.RequestUserUpdateDto;
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

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
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

        Authority authority = validarPermissaoExiste(dto.authority());

        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setSenha(encoder.encode(dto.senha()));
        usuario.setAtivo(true);
        usuario.adicionarAuthority(authority);

        return converterUsuarioParaResponseDto(usuarioRepository.save(usuario));
    }

    @Transactional
    public ResponseUsuarioDto updateUser(UUID idUsuario, RequestUserUpdateDto dto) {
        Usuario usuario = usuarioRepository.findById(idUsuario).orElseThrow(
                () -> new EntityNotFoundException("Nenhum usuário com este id encontrado!"));

        usuarioRepository.findByNome(dto.novoNome()).ifPresent(outroUsuario -> {
            if (!outroUsuario.getId().equals(usuario.getId())) {
                throw new ConflictEntityException("Já existe um usuário com este nome");
            }
        });

        usuario.setNome(dto.novoNome());
        usuario.setSenha(encoder.encode(dto.novaSenha()));

        atualizarAuthorities(usuario, dto.authorities());
        return converterUsuarioParaResponseDto(usuarioRepository.save(usuario));
    }

    public void deleteUser(UUID idUsuarioADeletar) {
        String responseErrorUserIdNotFound =
                String.format("Usuário com Id: %s não encontrado.", idUsuarioADeletar.toString());
        Usuario usuario = usuarioRepository.findById(idUsuarioADeletar)
                .orElseThrow(() -> new EntityNotFoundException(responseErrorUserIdNotFound));
        usuarioRepository.delete(usuario);
    }

    public List<ResponseUsuarioDto> findAllUsers() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return converterUsuarioParaRespondeDto(usuarios);
    }

    public List<ResponseUsuarioDto> findUsersByName(String nome) {
        return converterUsuarioParaRespondeDto(usuarioRepository.findByNomeContainingOrderByNome(nome));
    }

    public ResponseUsuarioDto findById(UUID id){
        Optional<Usuario> usuario = usuarioRepository.findById(id);

        if(usuario.isEmpty()){
            throw new EntityNotFoundException("Usuário com Id: " + id + " não encontrado.");
        }
        return converterUsuarioParaResponseDto(usuario.get());
    }

    private Authority validarPermissaoExiste(String authority) {
        return authorityRepository.findByNome(authority)
                .orElseThrow(() -> new EntityNotFoundException("Permissão não encontrada: " + authority));
    }

    private void atualizarAuthorities(Usuario usuario, Set<String> novaAuthorities) {
        usuario.getAuthorities().clear();

        novaAuthorities.forEach(authorityNome -> {
            Authority authority = validarPermissaoExiste(authorityNome);
            usuario.adicionarAuthority(authority);
        });
    }

    private ResponseUsuarioDto converterUsuarioParaResponseDto(Usuario usuario) {
        Set<String> authorities = usuario.getAuthorities().stream()
                .map(a -> a.getAuthority().getNome()).collect(Collectors.toSet());
        return new ResponseUsuarioDto(usuario.getId(), usuario.getNome(), usuario.getSenha(), usuario.isAtivo(), authorities);
    }

    private List<ResponseUsuarioDto> converterUsuarioParaRespondeDto(List<Usuario> usuarios) {
        return usuarios.stream()
                .map(this::converterUsuarioParaResponseDto).toList();
    }
}
