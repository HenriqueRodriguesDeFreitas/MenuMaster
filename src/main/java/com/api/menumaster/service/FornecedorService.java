package com.api.menumaster.service;

import com.api.menumaster.dtos.request.RequestFornecedorDto;
import com.api.menumaster.dtos.request.RequestFornecedorUpdateDto;
import com.api.menumaster.dtos.response.ResponseFornecedorDto;
import com.api.menumaster.exception.custom.ConflictException;
import com.api.menumaster.exception.custom.EntityNotFoundException;
import com.api.menumaster.model.Fornecedor;
import com.api.menumaster.repository.FornecedorRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class FornecedorService {

    private final FornecedorRepository fornecedorRepository;

    public FornecedorService(FornecedorRepository fornecedorRepository) {
        this.fornecedorRepository = fornecedorRepository;
    }

    @Transactional
    public ResponseFornecedorDto save(RequestFornecedorDto dto) {
        verificarExistente(dto);

        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setRazaoSocial(dto.razaoSocial());
        fornecedor.setNomeFantasia(dto.nomeFantasia());
        fornecedor.setCnpj(dto.cnpj());
        fornecedor.setInscricaoEstadual(dto.inscricaoEstadual());
        fornecedor.setContato(dto.contato());
        fornecedor.setEndereco(dto.endereco());
        fornecedor.setAtivo(true);

        var objectSave = fornecedorRepository.save(fornecedor);

        return convertEntityToDto(objectSave);

    }

    public ResponseFornecedorDto findByCnpj(String cnpj) {
        var response = fornecedorRepository.findByCnpj(cnpj)
                .orElseThrow(() -> new EntityNotFoundException("cnpj não cadastrado"));

        return convertEntityToDto(response);
    }

    public ResponseFornecedorDto findByIncricaoEstadual(String inscricaoEstadual) {
        var response = fornecedorRepository.findByInscricaoEstadual(inscricaoEstadual)
                .orElseThrow(() -> new EntityNotFoundException("inscrição estadual não cadastrado"));

        return convertEntityToDto(response);
    }

    public List<ResponseFornecedorDto> findByNomeFantasia(String nomeFantasia) {
        List<Fornecedor> response = fornecedorRepository.
                findByNomeFantasiaContainingIgnoreCase(nomeFantasia);

        return convertEntityToDto(response);
    }
    public List<ResponseFornecedorDto> findByRazaoSocial(String razaoSocial) {
        List<Fornecedor> response = fornecedorRepository.
                findByRazaoSocialContainingIgnoreCase(razaoSocial);

        return convertEntityToDto(response);
    }

    public List<ResponseFornecedorDto> findByFornecedoresAtivos(){
        List<Fornecedor> response = fornecedorRepository.findByIsAtivoTrue();

        return  convertEntityToDto(response);
    }

    public ResponseFornecedorDto update(UUID id, RequestFornecedorUpdateDto dto){
        Fornecedor fornecedor = fornecedorRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("fornecedor não encontrado"));

        fornecedor.setRazaoSocial(dto.razaoSocial());
        fornecedor.setNomeFantasia(dto.nomeFantasia());
        fornecedor.setCnpj(dto.cnpj());
        fornecedor.setInscricaoEstadual(dto.inscricaoEstadual());
        fornecedor.setEndereco(dto.endereco());
        fornecedor.setContato(dto.contato());
        fornecedor.setAtivo(dto.isAtivo());

        var response = fornecedorRepository.save(fornecedor);

        return convertEntityToDto(response);
    }

    public void deleteById(UUID id){
        var response = fornecedorRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("id não encontrado."));
        fornecedorRepository.deleteById(response.getId());
    }

    private ResponseFornecedorDto convertEntityToDto(Fornecedor fornecedor) {
        return new ResponseFornecedorDto(fornecedor.getId(),
                fornecedor.getRazaoSocial(), fornecedor.getNomeFantasia(),
                fornecedor.getCnpj(), fornecedor.getInscricaoEstadual(),
                fornecedor.getEndereco(), fornecedor.getContato(), fornecedor.isAtivo());
    }

    private List<ResponseFornecedorDto> convertEntityToDto(List<Fornecedor> fornecedores) {
        return fornecedores.stream()
                .map(f -> new ResponseFornecedorDto(f.getId(), f.getRazaoSocial(),
                        f.getNomeFantasia(), f.getCnpj(),
                        f.getInscricaoEstadual(), f.getEndereco(),
                        f.getContato(), f.isAtivo())).toList();
    }

    private void verificarExistente(RequestFornecedorDto dto) {
        fornecedorRepository.findByCnpj(dto.cnpj())
                .ifPresent( s-> {throw new ConflictException("cnpj já cadastrado");});
        fornecedorRepository.findByInscricaoEstadual(dto.inscricaoEstadual())
                .ifPresent(s -> {throw new ConflictException("IE já cadastrado");});
        fornecedorRepository.findByRazaoSocialIgnoreCase(dto.razaoSocial())
                .ifPresent(s -> {throw new ConflictException("razão social já cadastrado");});
        fornecedorRepository.findByNomeFantasiaIgnoreCase(dto.nomeFantasia())
                .ifPresent(s -> {throw new ConflictException("nome fantasia já cadastrado");});
    }
}