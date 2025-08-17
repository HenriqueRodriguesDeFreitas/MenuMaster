package com.api.menumaster.controller;

import com.api.menumaster.dtos.request.RequestUserUpdateDto;
import com.api.menumaster.dtos.request.RequestUsuarioDto;
import com.api.menumaster.dtos.response.ResponseUsuarioDto;
import com.api.menumaster.service.UsuarioService;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','USUARIO_CREATE')")
    public ResponseEntity<?> salvar(@RequestBody @Valid RequestUsuarioDto dto) {
        return ResponseEntity.ok(usuarioService.salvar(dto));
    }

    @PutMapping("/{idUsuario}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO_EDIT')")
    public ResponseEntity<ResponseUsuarioDto> updateUser(@PathVariable(value = "idUsuario",
                                                                 required = true) UUID idUsuario,
                                                         @RequestBody @Valid RequestUserUpdateDto dto) {
        return ResponseEntity.ok(usuarioService.updateUser(idUsuario, dto));
    }

    @DeleteMapping("/{idUsuario}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO_DELETE')")
    public ResponseEntity<Void> deleteUser(@PathVariable(value = "idUsuario", required = true)
                                           UUID idUsuario) {
        usuarioService.deleteUser(idUsuario);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO_READ')")
    public ResponseEntity<List<ResponseUsuarioDto>> findAllUsers(){
        return ResponseEntity.ok(usuarioService.findAllUsers());
    }

    @GetMapping("/byName/{name}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO_READ')")
    public ResponseEntity<List<ResponseUsuarioDto>> findUsersByName(@PathVariable("name") String name){
        return ResponseEntity.ok(usuarioService.findUsersByName(name));
    }
}
