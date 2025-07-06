package com.api.menumaster.service;

import com.api.menumaster.dtos.request.RequestEntradaNotaIngredienteDto;
import com.api.menumaster.dtos.response.ResponseEntradaIngredienteItem;
import com.api.menumaster.dtos.response.ResponseEntradaNotaIngredienteDto;
import com.api.menumaster.exception.custom.ConflictException;
import com.api.menumaster.exception.custom.EntityNotFoundException;
import com.api.menumaster.model.EntradaIngrediente;
import com.api.menumaster.model.EntradaIngredienteItem;
import com.api.menumaster.model.Fornecedor;
import com.api.menumaster.model.Ingrediente;
import com.api.menumaster.repository.EntradaIngredienteItemRepository;
import com.api.menumaster.repository.EntradaIngredienteRepository;
import com.api.menumaster.repository.FornecedorRepository;
import com.api.menumaster.repository.IngredienteRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EntradaNotaIngredienteService {

    private final FornecedorRepository fornecedorRepository;
    private final IngredienteRepository ingredienteRepository;
    private final EntradaIngredienteRepository entradaRepository;
    private final EntradaIngredienteItemRepository itemEntradaRespository;

    public EntradaNotaIngredienteService(FornecedorRepository fornecedorRepository,
                                         IngredienteRepository ingredienteRepository,
                                         EntradaIngredienteRepository entradaRepository,
                                         EntradaIngredienteItemRepository itemEntradaRespository) {
        this.fornecedorRepository = fornecedorRepository;
        this.ingredienteRepository = ingredienteRepository;
        this.entradaRepository = entradaRepository;
        this.itemEntradaRespository = itemEntradaRespository;
    }

    public ResponseEntradaNotaIngredienteDto entrada(RequestEntradaNotaIngredienteDto dto, UUID idFornecedor) {
       if(dto.itens() == null){
           throw new EntityNotFoundException("Entrada só possível com iten adicionados.");
       }

        Fornecedor fornecedor = fornecedorRepository.findById(idFornecedor)
                .orElseThrow(() -> new EntityNotFoundException("Nenhum forncedor com este id encontrado"));

        EntradaIngrediente entrada = new EntradaIngrediente(dto.dataEntrada(), dto.numeroNota(),
                dto.serieNota(), fornecedor, BigDecimal.ZERO, dto.observacao());

        List<EntradaIngredienteItem> itens = new ArrayList<>();

        if(entrada.verificarSeNotaPertenceAoFornecedor(entradaRepository)){
            throw new ConflictException("Já existe uma nota com este número para o fornecedor");
        }

        dto.itens().forEach(i -> {
            Ingrediente ingrediente = ingredienteRepository.findByCodigo(i.codigoIngrediente())
                    .orElseThrow(()-> new EntityNotFoundException("Ingrediente não encontrado"));
            entrada.addItemIngrediente(ingrediente, i.qtdEntrada(), i.valorCusto());
        });

        entrada.calcularTotalNota();

        EntradaIngrediente entradaSalva = entradaRepository.save(entrada);
        return converteObjetoParaDto(entradaSalva);
    }

    private ResponseEntradaNotaIngredienteDto converteObjetoParaDto(EntradaIngrediente entrada) {
        List<ResponseEntradaIngredienteItem> itens = entrada.getItens()
                .stream().map(i -> new ResponseEntradaIngredienteItem(
                        i.getIngrediente().getCodigo(), i.getQuantidade(), i.getCustoUnitario())).toList();

        return new ResponseEntradaNotaIngredienteDto(
                entrada.getFornecedor().getRazaoSocial(), entrada.getDataEntrada(),
                entrada.getNumeroNota(), entrada.getSerieNota(),
                entrada.getObservacao(), itens, entrada.getValorTotal()
        );
    }
}
