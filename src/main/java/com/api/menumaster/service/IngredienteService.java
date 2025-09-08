package com.api.menumaster.service;

import com.api.menumaster.dtos.request.RequestIngredienteDto;
import com.api.menumaster.dtos.request.RequestIngredienteUpdateDto;
import com.api.menumaster.dtos.response.ResponseIngredienteDto;
import com.api.menumaster.exception.custom.ConflictEntityException;
import com.api.menumaster.exception.custom.DadoPassadoNuloException;
import com.api.menumaster.exception.custom.EntityNotFoundException;
import com.api.menumaster.mappper.IngredienteMapper;
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
    private final IngredienteMapper ingredienteMapper;

    public IngredienteService(IngredienteRepository ingredienteRepository,
                              IngredienteMapper ingredienteMapper) {
        this.ingredienteRepository = ingredienteRepository;
        this.ingredienteMapper = ingredienteMapper;
    }

    @Transactional
    public ResponseIngredienteDto salvarNovoIngrediente(RequestIngredienteDto dto) {
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
    public ResponseIngredienteDto atualizarIngrediente(UUID id, RequestIngredienteUpdateDto dto) {
        var ingredienteUpdate = ingredienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ingrediente não cadastrado"));

        ingredienteRepository.findByCodigo(dto.codigo())
                .ifPresent(ingredienteExistente -> {
                    if (!ingredienteExistente.getId().equals(ingredienteUpdate.getId())) {
                        throw new ConflictEntityException("Já existe ingrediente com o código: " + dto.codigo());
                    }
                });
        ingredienteRepository.findByNomeIgnoreCase(dto.nome())
                .ifPresent(ingredienteExistente -> {
                    if (!ingredienteExistente.getNome().equals(ingredienteUpdate.getNome())) {
                        throw new ConflictEntityException("Já existe ingrediente com este nome: " + dto.nome());
                    }
                });

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

    public List<ResponseIngredienteDto> buscarTodosIngredientes() {
        List<Ingrediente> ingredientes = ingredienteRepository.findAll();
        return converteEntityToDto(ingredientes);
    }

    public List<ResponseIngredienteDto> buscarIngredientePorNome(String nome) {
        List<Ingrediente> ingredientes = ingredienteRepository.findByNomeContainingIgnoreCaseOrderByNome(nome);
        return converteEntityToDto(ingredientes);
    }

    public ResponseIngredienteDto buscarIngredientePorCodigo(Integer codigo) {
        Optional<Ingrediente> response = ingredienteRepository.findByCodigo(codigo);
        if (response.isPresent()) {
            return converteEntityToDto(response.get());
        } else {
            throw new EntityNotFoundException("Ingrediente não existe");
        }
    }

    public List<ResponseIngredienteDto> buscarIngredientePorDescricao(String descricao) {
        List<Ingrediente> ingredientes =
                ingredienteRepository.findByDescricaoContainingIgnoreCase(descricao);
        return converteEntityToDto(ingredientes);
    }

    public List<ResponseIngredienteDto> buscarIngredientePorPrecoCusto(BigDecimal precoCusto) {
        List<Ingrediente> ingredientes =
                ingredienteRepository.findByPrecoCusto(precoCusto);
        return converteEntityToDto(ingredientes);
    }

    public List<ResponseIngredienteDto> buscarIngredientePorPrecoVenda(BigDecimal precoVenda) {
        List<Ingrediente> ingredientes =
                ingredienteRepository.findByPrecoVenda(precoVenda);
        return converteEntityToDto(ingredientes);
    }

    public List<ResponseIngredienteDto> buscarIngredientePorPrecoCustoBetween(BigDecimal valorInicial,
                                                                              BigDecimal valorFinal) {
        validandoSeValoresSaoDiferentesDeNulo(valorInicial, valorFinal);

        if (valorFinal.compareTo(valorInicial) > 0) {
            List<Ingrediente> ingredientes =
                    ingredienteRepository.findByPrecoCustoBetween(valorInicial, valorFinal);
            return converteEntityToDto(ingredientes);
        } else {
            throw new IllegalArgumentException("Valor inicial precisa ser menor que o final.");
        }
    }

    public List<ResponseIngredienteDto> buscarIngredientePorPrecoVendaBetween(BigDecimal valorInicial,
                                                                              BigDecimal valorFinal) {

        validandoSeValoresSaoDiferentesDeNulo(valorInicial, valorFinal);

        if (valorFinal.compareTo(valorInicial) > 0) {
            List<Ingrediente> ingredientes =
                    ingredienteRepository.findByPrecoVendaBetween(valorInicial, valorFinal);
            return converteEntityToDto(ingredientes);
        } else {
            throw new IllegalArgumentException("Valor inicial precisa ser menor que final.");
        }
    }

    public List<ResponseIngredienteDto> buscarIngredienteIsAtivo() {
        List<Ingrediente> ingredientes =
                ingredienteRepository.findByIsAtivoTrue();
        return converteEntityToDto(ingredientes);
    }

    public List<ResponseIngredienteDto> buscarIngredienteIsInativo() {
        List<Ingrediente> ingredientes =
                ingredienteRepository.findByIsAtivoFalse();
        return converteEntityToDto(ingredientes);
    }

    public List<ResponseIngredienteDto> buscarIngredienteIsAdicional() {
        List<Ingrediente> ingredientes =
                ingredienteRepository.findByIsAdicionalTrue();
        return converteEntityToDto(ingredientes);
    }

    public List<ResponseIngredienteDto> buscarIngredienteIsNotAdicional() {
        List<Ingrediente> ingredientes =
                ingredienteRepository.findByIsAdicionalFalse();
        return converteEntityToDto(ingredientes);
    }

    public List<ResponseIngredienteDto> buscarIngredienteUnidadeMedida(UnidadeMedida unidadeMedida) {
        List<Ingrediente> ingredientes =
                ingredienteRepository.findByUnidadeMedida(unidadeMedida);
        return converteEntityToDto(ingredientes);
    }

    public List<ResponseIngredienteDto> buscarIngredienteControlarEstoqueIsTrue() {
        List<Ingrediente> ingredientes =
                ingredienteRepository.findByControlarEstoqueTrue();
        return converteEntityToDto(ingredientes);
    }

    public List<ResponseIngredienteDto> buscarIngredienteControlarEstoqueIsFalse() {
        List<Ingrediente> ingredientes =
                ingredienteRepository.findByControlarEstoqueFalse();
        return converteEntityToDto(ingredientes);
    }

    public void deleteById(UUID id) {
        var ingrediente = ingredienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ingrediente não existe"));
        ingredienteRepository.deleteById(ingrediente.getId());
    }

    private ResponseIngredienteDto converteEntityToDto(Ingrediente ingrediente) {
        return ingredienteMapper.toResponse(ingrediente);
    }

    private List<ResponseIngredienteDto> converteEntityToDto(List<Ingrediente> ingredientes) {
        return ingredientes.stream()
                .map(this::converteEntityToDto).toList();
    }

    private static void validandoSeValoresSaoDiferentesDeNulo(BigDecimal valorInicial, BigDecimal valorFinal) {
        if (valorInicial == null || valorFinal == null)
            throw new DadoPassadoNuloException("Valor inicial ou final não pode ser nulo.");
    }
}
