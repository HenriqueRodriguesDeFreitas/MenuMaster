package com.api.menumaster.service;

import com.api.menumaster.dtos.request.RequestIngredienteDto;
import com.api.menumaster.dtos.request.RequestIngredienteUpdateDto;
import com.api.menumaster.dtos.response.ResponseIngredienteDto;
import com.api.menumaster.exception.custom.ConflictException;
import com.api.menumaster.exception.custom.EntityNotFoundException;
import com.api.menumaster.model.Ingrediente;
import com.api.menumaster.model.enums.UnidadeMedida;
import com.api.menumaster.repository.IngredienteRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class IngredienteService {

    private final IngredienteRepository ingredienteRepository;

    public IngredienteService(IngredienteRepository ingredienteRepository) {
        this.ingredienteRepository = ingredienteRepository;
    }

    @Transactional
    public ResponseIngredienteDto save(RequestIngredienteDto dto) {
        ingredienteRepository.findByCodigo(dto.codigo())
                .ifPresent(i -> {
                    throw new ConflictEntityException(" Já ingrediente com o codigo: " + i.getCodigo());
                });
        ingredienteRepository.findByNomeIgnoreCase(dto.nome())
                .ifPresent(i -> {
                    throw new ConflictEntityException("ingrediente já cadastrado");
                });

        Ingrediente novoIngrediente = new Ingrediente();
        novoIngrediente.setCodigo(dto.codigo());
        novoIngrediente.setNome(dto.nome());
        novoIngrediente.setDescricao(dto.descricao());
        novoIngrediente.setPrecoCusto(dto.precoCusto());
        novoIngrediente.setPrecoVenda(dto.precoVenda());
        novoIngrediente.setAdicional(dto.isAdicional());
        novoIngrediente.setUnidadeMedida(UnidadeMedida.valueOf(dto.unidadeMedida().name().toUpperCase()));
        novoIngrediente.setControlarEstoque(dto.controlarEstoque());


        var response = ingredienteRepository.save(novoIngrediente);
        return converteEntityToDto(response);
    }

    @Transactional
    public ResponseIngredienteDto update(UUID id, RequestIngredienteUpdateDto dto) {
        ingredienteRepository.findByCodigo(dto.codigo())
                .ifPresent(i -> {
                    throw new ConflictEntityException(" Já ingrediente com o codigo: " + i.getCodigo());
                });
        var ingredienteUpdate = ingredienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ingrediente não cadastrado"));

        ingredienteUpdate.setNome(dto.nome());
        ingredienteUpdate.setDescricao(dto.descricao());
        ingredienteUpdate.setPrecoCusto(dto.precoCusto());
        ingredienteUpdate.setPrecoVenda(dto.precoVenda());
        ingredienteUpdate.setAtivo(dto.isAtivo());
        ingredienteUpdate.setAdicional(dto.isAdicional());
        ingredienteUpdate.setUnidadeMedida(dto.unidadeMedida());
        ingredienteUpdate.setControlarEstoque(dto.controlarEstoque());

  var response = ingredienteRepository.save(ingredienteUpdate);
  return converteEntityToDto(response);
    }

    public List<ResponseIngredienteDto> findAll() {
        List<Ingrediente> ingredientes = ingredienteRepository.findAll();
        return converteEntityToDto(ingredientes);
    }

    public List<ResponseIngredienteDto> findByNome(String nome) {
        List<Ingrediente> ingredientes = ingredienteRepository.findByNomeContainingIgnoreCase(nome);
        return converteEntityToDto(ingredientes);
    }

    public ResponseIngredienteDto findByCodigo(Integer codigo) {
        Optional<Ingrediente> response = ingredienteRepository.findByCodigo(codigo);
        if(response.isPresent()){
            return converteEntityToDto(response.get());
        }else{
            throw new EntityNotFoundException("Ingrediente não existe");
        }
    }

    public List<ResponseIngredienteDto> findByDescricao(String descricao) {
        List<Ingrediente> ingredientes =
                ingredienteRepository.findByDescricaoContainingIgnoreCase(descricao);
        return converteEntityToDto(ingredientes);
    }

    public List<ResponseIngredienteDto> findByPrecoCusto(BigDecimal precoCusto) {
        List<Ingrediente> ingredientes =
                ingredienteRepository.findByPrecoCusto(precoCusto);
        return converteEntityToDto(ingredientes);
    }

    public List<ResponseIngredienteDto> findByPrecoVenda(BigDecimal precoVenda) {
        List<Ingrediente> ingredientes =
                ingredienteRepository.findByPrecoVenda(precoVenda);
        return converteEntityToDto(ingredientes);
    }

    public List<ResponseIngredienteDto> findByIsAtivo() {
        List<Ingrediente> ingredientes =
                ingredienteRepository.findByIsAtivoTrue();
        return converteEntityToDto(ingredientes);
    }

    public List<ResponseIngredienteDto> findByIsInativo() {
        List<Ingrediente> ingredientes =
                ingredienteRepository.findByIsAtivoFalse();
        return converteEntityToDto(ingredientes);
    }

    public List<ResponseIngredienteDto> findByIsAdicional() {
        List<Ingrediente> ingredientes =
                ingredienteRepository.findByIsAdicionalTrue();
        return converteEntityToDto(ingredientes);
    }

    public List<ResponseIngredienteDto> findByIsNotAdicional() {
        List<Ingrediente> ingredientes =
                ingredienteRepository.findByIsAdicionalFalse();
        return converteEntityToDto(ingredientes);
    }

    public List<ResponseIngredienteDto> findByUnidadeMedida(UnidadeMedida unidadeMedida) {
        List<Ingrediente> ingredientes =
                ingredienteRepository.findByUnidadeMedida(unidadeMedida);
        return converteEntityToDto(ingredientes);
    }

    public List<ResponseIngredienteDto> findByControlarEstoqueIsTrue() {
        List<Ingrediente> ingredientes =
                ingredienteRepository.findByControlarEstoqueTrue();
        return converteEntityToDto(ingredientes);
    }

    public List<ResponseIngredienteDto> findByControlarEstoqueIsFalse() {
        List<Ingrediente> ingredientes =
                ingredienteRepository.findByControlarEstoqueFalse();
        return converteEntityToDto(ingredientes);
    }

    public void deleteById(UUID id){
        var ingrediente = ingredienteRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Ingrediente não existe"));
        ingredienteRepository.deleteById(ingrediente.getId());
    }

    private ResponseIngredienteDto converteEntityToDto(Ingrediente ingrediente) {
        return new ResponseIngredienteDto(
                ingrediente.getId(), ingrediente.getCodigo(), ingrediente.getNome(),
                ingrediente.getDescricao(), ingrediente.getEstoque(),
                ingrediente.getPrecoCusto(), ingrediente.getPrecoVenda(),
                ingrediente.isAtivo(), ingrediente.isAdicional(),
                ingrediente.getUnidadeMedida(), ingrediente.isControlarEstoque()
        );
    }

    private List<ResponseIngredienteDto> converteEntityToDto(List<Ingrediente> ingredientes) {
        return ingredientes.stream()
                .map(i ->
                        new ResponseIngredienteDto(i.getId(), i.getCodigo(), i.getNome(),
                                i.getDescricao(), i.getEstoque(), i.getPrecoCusto(), i.getPrecoVenda(),
                                i.isAtivo(), i.isAdicional(), i.getUnidadeMedida(), i.isControlarEstoque())).toList();
    }
}
