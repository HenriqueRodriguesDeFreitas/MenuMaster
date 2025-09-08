package com.api.menumaster.controller;

import com.api.menumaster.dtos.request.RequestFornecedorDto;
import com.api.menumaster.dtos.request.RequestFornecedorUpdateDto;
import com.api.menumaster.service.FornecedorService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("fornecedor")
public class FornecedorController {

    private final FornecedorService fornecedorService;

    public FornecedorController(FornecedorService fornecedorService) {
        this.fornecedorService = fornecedorService;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'FORNECEDOR_CREATE')")
    public ResponseEntity<?> save(@RequestBody @Valid RequestFornecedorDto dto) {
        return ResponseEntity.ok(fornecedorService.salvarNovoFornecedor(dto));
    }

    @GetMapping("/byCnpj")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'FORNECEDOR_READ')")
    public ResponseEntity<?> findByCnpj(@RequestParam("cnpj")
                                        @Size(min = 14, max = 14) String cnpj) {
        return ResponseEntity.ok(fornecedorService.buscarFornecedorPorCnpj(cnpj));
    }

    @GetMapping("/byInscricaoEstadual")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'FORNECEDOR_READ')")
    public ResponseEntity<?> findByInscricaoEstadual(@RequestParam("inscricaoEstadual")
                                                     @Size(min = 9, max = 9)
                                                     String inscricaoEstadual) {
        return ResponseEntity.ok(fornecedorService.buscarFornecedorPorInscricaoEstadual(inscricaoEstadual));
    }

    @GetMapping("/byNomeFantasia")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'FORNECEDOR_READ')")
    public ResponseEntity<List<?>> findByNomeFantasia(@RequestParam("nomeFantasia")
                                                      String nomeFantasia) {
        return ResponseEntity.ok(fornecedorService.buscarFornecedorPorNomeFantasia(nomeFantasia));
    }

    @GetMapping("/byRazaoSocial")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'FORNECEDOR_READ')")
    public ResponseEntity<List<?>> findByRazaoSocial(@RequestParam("razaoSocial")
                                                     String razaoSocial) {
        return ResponseEntity.ok(fornecedorService.buscarFornecedorPorRazaoSocial(razaoSocial));
    }

    @GetMapping("/byFornecedoresAtivos")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'FORNECEDOR_READ')")
    public ResponseEntity<List<?>> findByFornecedoresAtivos() {
        return ResponseEntity.ok(fornecedorService.buscarFornecedorPorFornecedoresAtivos());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'FORNECEDOR_UPDATE')")
    public ResponseEntity<?> update(@PathVariable("id") UUID id,
                                    @RequestBody @Valid RequestFornecedorUpdateDto dto) {
        return ResponseEntity.ok(fornecedorService.atualizarFornecedor(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'FORNECEDOR_DELETE')")
    public ResponseEntity<Void> deleteById(@PathVariable("id") UUID id){
        fornecedorService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }


}
