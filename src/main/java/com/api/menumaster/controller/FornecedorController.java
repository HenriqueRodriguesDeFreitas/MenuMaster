package com.api.menumaster.controller;

import com.api.menumaster.dtos.request.RequestFornecedorDto;
import com.api.menumaster.dtos.request.RequestFornecedorUpdateDto;
import com.api.menumaster.service.FornecedorService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> save(@RequestBody @Valid RequestFornecedorDto dto) {
        return ResponseEntity.ok(fornecedorService.save(dto));
    }

    @GetMapping("/byCnpj")
    public ResponseEntity<?> findByCnpj(@RequestParam("cnpj")
                                        @Size(min = 14, max = 14) String cnpj) {
        return ResponseEntity.ok(fornecedorService.findByCnpj(cnpj));
    }

    @GetMapping("/byInscricaoEstadual")
    public ResponseEntity<?> findByInscricaoEstadual(@RequestParam("inscricaoEstadual")
                                                     @Size(min = 9, max = 9)
                                                     String inscricaoEstadual) {
        return ResponseEntity.ok(fornecedorService.findByIncricaoEstadual(inscricaoEstadual));
    }

    @GetMapping("/byNomeFantasia")
    public ResponseEntity<List<?>> findByNomeFantasia(@RequestParam("nomeFantasia")
                                                      String nomeFantasia) {
        return ResponseEntity.ok(fornecedorService.findByNomeFantasia(nomeFantasia));
    }

    @GetMapping("/byRazaoSocial")
    public ResponseEntity<List<?>> findByRazaoSocial(@RequestParam("razaoSocial")
                                                     String razaoSocial) {
        return ResponseEntity.ok(fornecedorService.findByRazaoSocial(razaoSocial));
    }

    @GetMapping("/byFornecedoresAtivos")
    public ResponseEntity<List<?>> findByFornecedoresAtivos() {
        return ResponseEntity.ok(fornecedorService.findByFornecedoresAtivos());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") UUID id,
                                    @RequestBody @Valid RequestFornecedorUpdateDto dto) {
        return ResponseEntity.ok(fornecedorService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") UUID id){
        fornecedorService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }


}
