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
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class EntradaIngredienteService {

    private final FornecedorRepository fornecedorRepository;
    private final IngredienteRepository ingredienteRepository;
    private final EntradaIngredienteRepository entradaRepository;

    public EntradaIngredienteService(FornecedorRepository fornecedorRepository,
                                     IngredienteRepository ingredienteRepository,
                                     EntradaIngredienteRepository entradaRepository) {
        this.fornecedorRepository = fornecedorRepository;
        this.ingredienteRepository = ingredienteRepository;
        this.entradaRepository = entradaRepository;
    }

    @Transactional
    public ResponseEntradaNotaIngredienteDto entrada(UUID idFornecedor, RequestEntradaNotaIngredienteDto dto) {
        if (dto.itens() == null) {
            throw new EntityNotFoundException("Entrada só possível com iten adicionados.");
        }

        Fornecedor fornecedor = fornecedorRepository.findById(idFornecedor)
                .orElseThrow(() -> new EntityNotFoundException("Nenhum forncedor com este id encontrado"));

        EntradaIngrediente entrada = new EntradaIngrediente(dto.dataEntrada(), dto.numeroNota(),
                dto.serieNota(), fornecedor, BigDecimal.ZERO, dto.observacao());


        if (entrada.verificarSeNotaPertenceAoFornecedor(entradaRepository)) {
            throw new ConflictException("Já existe uma nota com este número para o fornecedor");
        }

        dto.itens().forEach(i -> {
            Ingrediente ingrediente = ingredienteRepository.findByCodigo(i.codigoIngrediente())
                    .orElseThrow(() -> new EntityNotFoundException("Ingrediente não encontrado"));
            entrada.addItemIngrediente(ingrediente, i.qtdEntrada(), i.valorCusto());
        });

        entrada.calcularTotalNota();
         entrada.getItens().forEach(i ->{
             Ingrediente ingrediente = ingredienteRepository.findByCodigo(i.getIngrediente().getCodigo())
                     .orElseThrow(()-> new EntityNotFoundException("Erro em atualizar ingrediente: " +
                             i.getIngrediente().getNome()));
             ingrediente.setPrecoCusto(i.getCustoUnitario());
             ingrediente.setPrecoVenda(ingrediente.getPrecoCusto().multiply(BigDecimal.valueOf(1.1)));

             if(ingrediente.getEstoque() == null){
                 ingrediente.setEstoque(BigDecimal.ZERO.add(i.getQuantidade()));
             }else{
                 ingrediente.setEstoque(ingrediente.getEstoque().add(i.getQuantidade()));
             }
         });

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
