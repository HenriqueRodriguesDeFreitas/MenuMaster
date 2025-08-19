package com.api.menumaster.service;

import com.api.menumaster.dtos.request.RequestUserUpdateDto;
import com.api.menumaster.dtos.request.RequestUsuarioDto;
import com.api.menumaster.dtos.response.ResponseUsuarioDto;
import com.api.menumaster.exception.custom.ConflictEntityException;
import com.api.menumaster.exception.custom.EntityNotFoundException;
import com.api.menumaster.model.Authority;
import com.api.menumaster.model.Usuario;
import com.api.menumaster.model.UsuarioAuthority;
import com.api.menumaster.repository.AuthorityRepository;
import com.api.menumaster.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private AuthorityRepository authorityRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private RequestUsuarioDto requestUserDto;
    private RequestUserUpdateDto requestUserUpdateDto;
    private Authority authority;
    private Usuario usuarioSalvo;
    private List<ResponseUsuarioDto> responseUsuarioDtoList;
    private ResponseUsuarioDto responseUsuarioDto;


    @BeforeEach
    void setUp() {
        requestUserDto = new RequestUsuarioDto("usuarioTeste", "senha123", "ADMIN");
        requestUserUpdateDto = new RequestUserUpdateDto("novoNome", "novaSenha",
                Set.of("ADMIN", "OPERADOR"));

        authority = new Authority();
        authority.setNome("ADMIN");

        usuarioSalvo = new Usuario();
        usuarioSalvo.setId(UUID.randomUUID());
        usuarioSalvo.setNome("usuarioTeste");
        usuarioSalvo.setSenha("senhaCriptografada");
        usuarioSalvo.adicionarAuthority(authority);
    }

    @Test
    void salvar_deveRetornarResponseUsuarioDto_quandoSucesso() {
        String senhaOriginal = "senha123";
        String senhaCriptografada = "hashDaSenha";

        when(usuarioRepository.findByNome(anyString())).thenReturn(Optional.empty());
        when(authorityRepository.findByNome(anyString())).thenReturn(Optional.of(authority));
        when(passwordEncoder.encode(senhaOriginal)).thenReturn(senhaCriptografada);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioSalvo);
        responseUsuarioDto = usuarioService.salvar(requestUserDto);

        assertNotNull(responseUsuarioDto);
        assertEquals(usuarioSalvo.getId(), responseUsuarioDto.id(), "O id diferente do esperado.");
        assertEquals(usuarioSalvo.getNome(), responseUsuarioDto.nome(), "O nome é diferente do esperado.");
        assertEquals(usuarioSalvo.getSenha(), responseUsuarioDto.senha(), "A senha é diferente do esperado.");
        assertTrue(responseUsuarioDto.authority().contains(authority.getNome()), "A authorities não correspondem");

        verify(usuarioRepository, times(1)).findByNome(requestUserDto.nome());
        verify(authorityRepository, times(1)).findByNome(requestUserDto.authority());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void salvar_deveLancarConflictEntityException__quandoUsuarioExiste() {
        when(usuarioRepository.findByNome(requestUserDto.nome())).thenReturn(Optional.of(usuarioSalvo));

        assertThrows(ConflictEntityException.class, () -> usuarioService.salvar(requestUserDto));

        verify(usuarioRepository, times(1)).findByNome(requestUserDto.nome());
        verifyNoMoreInteractions(authorityRepository, passwordEncoder, usuarioRepository);
    }

    @Test
    void salvar_deveLacarEntityNotFoundException_quandoAuthorityNaoExiste() {
        when(authorityRepository.findByNome(requestUserDto.authority())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> usuarioService.salvar(requestUserDto));

        verify(authorityRepository, times(1)).findByNome(requestUserDto.authority());
        verifyNoInteractions(passwordEncoder);
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void updateUser_devePermitir_quandoNovoNomeIgualAoAtual() {
        when(usuarioRepository.findById(usuarioSalvo.getId())).thenReturn(Optional.of(usuarioSalvo));
        when(usuarioRepository.findByNome(usuarioSalvo.getNome())).thenReturn(Optional.of(usuarioSalvo));
        when(authorityRepository.findByNome("ADMIN")).thenReturn(Optional.of(authority));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioSalvo);

        requestUserUpdateDto = new RequestUserUpdateDto(
                usuarioSalvo.getNome(), // mesmo nome
                "novaSenha",
                Set.of("ADMIN")
        );

        ResponseUsuarioDto response = usuarioService.updateUser(usuarioSalvo.getId(), requestUserUpdateDto);

        assertNotNull(response, "Objeto está retornando nulo");
        assertEquals(usuarioSalvo.getId(), response.id(), "Id diferente do esperado.");
        assertEquals(usuarioSalvo.getNome(), response.nome(), "Nome diferente do esperado.");
        assertEquals(usuarioSalvo.getNome(), response.nome(), "Nome diferente do esperado.");
        assertTrue(response.authority().contains("ADMIN"), "Authority não corresponde");

        verify(usuarioRepository, times(1)).findById(usuarioSalvo.getId());
        verify(usuarioRepository, times(1)).findByNome(requestUserUpdateDto.novoNome());
        verify(passwordEncoder, times(1)).encode(requestUserUpdateDto.novaSenha());
        verify(authorityRepository, times(1)).findByNome("ADMIN");
        verify(usuarioRepository, times(1)).save(usuarioSalvo);
    }

    @Test
    void updateUser_deveRetornarResponseUsuarioDto_quandoSucesso() {
        Authority novaAuthority = new Authority();
        novaAuthority.setNome("OPERADOR");
        usuarioSalvo.adicionarAuthority(novaAuthority);

        when(usuarioRepository.findById(usuarioSalvo.getId())).thenReturn(Optional.of(usuarioSalvo));
        when(usuarioRepository.findByNome(requestUserUpdateDto.novoNome())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(requestUserUpdateDto.novaSenha())).thenReturn("hashNovaSenha");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioSalvo);
        when(authorityRepository.findByNome("ADMIN")).thenReturn(Optional.of(authority));
        when(authorityRepository.findByNome("OPERADOR")).thenReturn(Optional.of(novaAuthority));

         responseUsuarioDto = usuarioService.updateUser(usuarioSalvo.getId(), requestUserUpdateDto);

        assertNotNull(responseUsuarioDto, "O objeto está retornando null");
        assertEquals(usuarioSalvo.getId(), responseUsuarioDto.id(), "id do usuário não corresponde");
        assertEquals(usuarioSalvo.getNome(), responseUsuarioDto.nome(), "nome do usuário não corresponde");
        assertEquals("hashNovaSenha", responseUsuarioDto.senha(), "senha do usuário não corresponde");

        Set<String> authoritiesEsperadas = Set.of("ADMIN", "OPERADOR");
        assertEquals(authoritiesEsperadas, responseUsuarioDto.authority(), "authorities do usuário não corresponde");

        verify(usuarioRepository, times(1)).findById(usuarioSalvo.getId());
        verify(usuarioRepository, times(1)).findByNome(requestUserUpdateDto.novoNome());
        verify(authorityRepository, times(1)).findByNome("ADMIN");
        verify(authorityRepository, times(1)).findByNome("OPERADOR");
        verify(usuarioRepository, times(1)).save(usuarioSalvo);

    }

    @Test
    void updateUser_deveRetornarEntityNotFoundException_quandoIdUsuarioNaoEncontrado() {
        UUID idInexistente = UUID.randomUUID();
        when(usuarioRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            usuarioService.updateUser(idInexistente, requestUserUpdateDto);
        });

        verify(usuarioRepository, times(1)).findById(idInexistente);
        verifyNoMoreInteractions(usuarioRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void updateUser_deveRetornarConflictEntityException_quandoNomeJaCadastrado() {
        UUID idUsuarioExistente = usuarioSalvo.getId();
        String nomeOriginal = usuarioSalvo.getNome();
        String senhaOriginal = usuarioSalvo.getSenha();
        Set<Authority> authoritiesOriginal = usuarioSalvo.getAuthorities().stream()
                .map(UsuarioAuthority::getAuthority).collect(Collectors.toSet());


        Usuario outroUsuario = new Usuario();
        outroUsuario.setId(UUID.randomUUID()); // ID diferente
        outroUsuario.setNome(requestUserUpdateDto.novoNome());

        when(usuarioRepository.findById(usuarioSalvo.getId())).thenReturn(Optional.of(usuarioSalvo));
        when(usuarioRepository.findByNome(requestUserUpdateDto.novoNome())).thenReturn(Optional.of(outroUsuario));

        ConflictEntityException exception = assertThrows(ConflictEntityException.class, () -> {
            usuarioService.updateUser(usuarioSalvo.getId(), requestUserUpdateDto);
        });

        assertTrue(exception.getMessage().contains("Já existe um usuário com este nome"),
                "Mensagem de erro incorreta");

        // Verifica que os dados do usuário não foram alterados
        assertEquals(idUsuarioExistente, usuarioSalvo.getId(), "Id não deveria ser alterado");
        assertEquals(nomeOriginal, usuarioSalvo.getNome(), "Nome foi alterado indevidamente");
        assertEquals(senhaOriginal, usuarioSalvo.getSenha(), "Senha foi alterada indevidamente");
        assertEquals(authoritiesOriginal, usuarioSalvo.getAuthorities().stream()
                        .map(UsuarioAuthority::getAuthority).collect(Collectors.toSet()),
                "Authorities foram alteradas indevidamente");

        verify(usuarioRepository, times(1)).findById(usuarioSalvo.getId());
        verify(usuarioRepository, times(1)).findByNome(requestUserUpdateDto.novoNome());
        verifyNoInteractions(passwordEncoder);
        verifyNoMoreInteractions(authorityRepository);
        verifyNoMoreInteractions(usuarioRepository);
    }

    @Test
    void findAllUsers_deveRetornarListaResponseUsuarioDto_quandoUsuariosCadastrados() {
        Usuario user1 = new Usuario("user1", passwordEncoder.encode("senha1"));
        Usuario user2 = new Usuario("user2", passwordEncoder.encode("senha2"));
        List<Usuario> usuarioList = List.of(user1, user2);

        when(usuarioRepository.findAll()).thenReturn(usuarioList);


        responseUsuarioDtoList = usuarioService.findAllUsers();

        assertNotNull(responseUsuarioDtoList, "Lista não deveria ser nula.");
        assertEquals(usuarioList.size(), responseUsuarioDtoList.size());
        assertAll("Validação de usuários retornados",
                () -> assertEquals(user1.getNome(), responseUsuarioDtoList.getFirst().nome()),
                () -> assertEquals(user1.getSenha(), responseUsuarioDtoList.getFirst().senha()),
                () -> assertEquals(user2.getNome(), responseUsuarioDtoList.get(1).nome()),
                () -> assertEquals(user2.getSenha(), responseUsuarioDtoList.get(1).senha())
        );

        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void findAllUsers_deveRetornarListaVazia_quandoNaoExistemUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(List.of());

        responseUsuarioDtoList = usuarioService.findAllUsers();

        assertNotNull(responseUsuarioDtoList, "O response deveria estar vazio");
        assertTrue(responseUsuarioDtoList.isEmpty(), "A lista deveria estar vazia.");
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void deleteUser_naoDeveRetornarNada_quandoSucesso() {
        when(usuarioRepository.findById(usuarioSalvo.getId())).thenReturn(Optional.of(usuarioSalvo));

        usuarioService.deleteUser(usuarioSalvo.getId());
        verify(usuarioRepository, times(1)).delete(usuarioSalvo);
    }

    @Test
    void deleteUser_deveRetornarEntityNotFoundException_quandoUsuarioNaoEncontrado() {
        UUID idUsuarioNaoExistente = UUID.randomUUID();
        when(usuarioRepository.findById(idUsuarioNaoExistente)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> usuarioService.deleteUser(idUsuarioNaoExistente));

        assertTrue(exception.getMessage().contains("Usuário com Id: " + idUsuarioNaoExistente.toString() + " não encontrado."), "Mensagens não coincidem.");
        verifyNoMoreInteractions(usuarioRepository);
    }

    @Test
    void findUsersByName_deveRetornarListaResponseUsuarioDto_quandoExistemUsuario() {
        Usuario user1 = new Usuario("user1", "senha1");
        Usuario user2 = new Usuario("user2", "senha2");
        List<Usuario> usuarioList = List.of(user1, user2);
        when(usuarioRepository.findByNomeContainingOrderByNome("user")).thenReturn(usuarioList);

        responseUsuarioDtoList = usuarioService.findUsersByName("user");

        assertNotNull(responseUsuarioDtoList, "lista não deveria ser vazia.");
        assertAll("Validação de usuários retornados",
                () -> assertTrue(user1.getNome().contains(responseUsuarioDtoList.getFirst().nome())),
                () -> assertTrue(user2.getNome().contains(responseUsuarioDtoList.get(1).nome())),
                () -> assertEquals(user1.getSenha(), responseUsuarioDtoList.getFirst().senha()),
                () -> assertEquals(user2.getSenha(), responseUsuarioDtoList.get(1).senha()));

        verify(usuarioRepository, times(1)).findByNomeContainingOrderByNome("user");
    }

    @Test
    void findById_deveRetornarResponseUsuarioDto_quandoUsuarioEncontrado() {
        UUID idUsuarioExistente = usuarioSalvo.getId();
        when(usuarioRepository.findById(idUsuarioExistente)).thenReturn(Optional.of(usuarioSalvo));

        responseUsuarioDto = usuarioService.findById(idUsuarioExistente);

        assertNotNull(responseUsuarioDto);
        assertEquals(usuarioSalvo.getId(), responseUsuarioDto.id(), "Id não coincidem");
        assertEquals(usuarioSalvo.getNome(), responseUsuarioDto.nome(), "Nomes não coincidem");
        assertEquals(usuarioSalvo.getSenha(), responseUsuarioDto.senha(), "Senhas não coincidem");

        verify(usuarioRepository, times(1)).findById(idUsuarioExistente);
    }

    @Test
    void findById_deveRetornarEntityNotFoundExeption_quandoUsuarioNaoEncontrado() {
        UUID idUsuarioInexistente = UUID.randomUUID();
        when(usuarioRepository.findById(idUsuarioInexistente)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> usuarioService.findById(idUsuarioInexistente));

        assertEquals(exception.getMessage(), "Usuário com Id: " + idUsuarioInexistente + " não encontrado.");
        verifyNoMoreInteractions(usuarioRepository);
    }
}