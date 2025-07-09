package com.api.menumaster.service;

import com.api.menumaster.dtos.request.RequestEntradaNotaIngredienteDto;
import com.api.menumaster.dtos.request.RequestUpdateItensEntradaIngredienteDto;
import com.api.menumaster.dtos.response.ResponseEntradaIngredienteItem;
import com.api.menumaster.dtos.response.ResponseEntradaNotaIngredienteDto;
import com.api.menumaster.exception.custom.ConflictException;
import com.api.menumaster.exception.custom.EntityNotFoundException;
import com.api.menumaster.model.EntradaIngrediente;
import com.api.menumaster.model.EntradaIngredienteItem;
import com.api.menumaster.model.Fornecedor;
import com.api.menumaster.model.Ingrediente;
import com.api.menumaster.repository.EntradaIngredienteRepository;
import com.api.menumaster.repository.FornecedorRepository;
import com.api.menumaster.repository.IngredienteRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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

        if(!fornecedor.isAtivo()){
            throw new ConflictException("Fornecedor inativo!");
        }

        EntradaIngrediente entrada = new EntradaIngrediente(dto.dataEntrada(), dto.numeroNota(),
                dto.serieNota(), fornecedor, BigDecimal.ZERO, dto.observacao());


        if (entrada.verificarSeNotaPertenceAoFornecedor(entradaRepository)) {
            throw new ConflictException("Já existe uma nota com este número para o fornecedor");
        }

        processarItensAdicionados(dto, entrada);

        entrada.calcularTotalNota();
        atualizarPrecoEstoqueIngrediente(entrada);

        EntradaIngrediente entradaSalva = entradaRepository.save(entrada);
        return converteObjetoParaDto(entradaSalva);
    }

    @Transactional
    public ResponseEntradaNotaIngredienteDto atualizarItensDaNota(
            String numeroNota, RequestUpdateItensEntradaIngredienteDto dto) {

        EntradaIngrediente entrada = entradaRepository.findByNumeroNota(numeroNota)
                .orElseThrow(() -> new EntityNotFoundException("Nota não encontrada"));

        if (!entrada.getFornecedor().isAtivo()) {
            throw new ConflictException("Não é possivel modificar notas de fornecedor inativo.");
        }

        List<EntradaIngredienteItem> itensOriginais = new ArrayList<>(entrada.getItens());

        entrada.getItens().clear();
        processarItensAdicionados(dto, entrada);

        entrada.calcularTotalNota();

        atualizarPrecoEstoqueComDiferenca(entrada, itensOriginais);

        return converteObjetoParaDto(entradaRepository.save(entrada));
    }

    public List<ResponseEntradaNotaIngredienteDto> findAll() {
        List<EntradaIngrediente> entradas = entradaRepository.findAll();
        return converteObjetoParaDto(entradas);
    }

    public List<ResponseEntradaNotaIngredienteDto> findByDataEntrada(LocalDate dataEntrada) {
        List<EntradaIngrediente> entradas = entradaRepository.findByDataEntrada(dataEntrada);
        return converteObjetoParaDto(entradas);
    }

    public List<ResponseEntradaNotaIngredienteDto> findByFornecedorRazaoSocial(String razaoSocial) {
        List<EntradaIngrediente> entradas = entradaRepository.findByFornecedorRazaoSocial(razaoSocial);
        return converteObjetoParaDto(entradas);
    }

    public List<ResponseEntradaNotaIngredienteDto> findByFornecedorNomeFantasia(String nomeFantasia) {
        List<EntradaIngrediente> entradas = entradaRepository.findByFornecedorNomeFantasia(nomeFantasia);
        return converteObjetoParaDto(entradas);
    }

    public ResponseEntradaNotaIngredienteDto findByNumeroNota(String numeroNota) {
        EntradaIngrediente entrada = entradaRepository.findByNumeroNota(numeroNota)
                .orElseThrow(() -> new EntityNotFoundException("Não existe nota com esta numeração"));
        return converteObjetoParaDto(entrada);
    }

    public List<ResponseEntradaNotaIngredienteDto> findByValorTotalNota(BigDecimal valorTotal) {
        List<EntradaIngrediente> entradas = entradaRepository.findByValorTotal(valorTotal);
        return converteObjetoParaDto(entradas);
    }

    public void deleteByNumeroNota(String numeroNota) {
        EntradaIngrediente entrada = entradaRepository.findByNumeroNota(numeroNota)
                .orElseThrow(() -> new EntityNotFoundException("Não existem notas com este número"));

        for (EntradaIngredienteItem i : entrada.getItens()) {
            Ingrediente ingrediente = ingredienteRepository.findByCodigo(i.getIngrediente().getCodigo())
                    .orElseThrow(() -> new EntityNotFoundException("Produto da entrada não encontrado em banco de dados: " + i.getIngrediente().getNome()));

            BigDecimal qtdExtornado = ingrediente.getEstoque().subtract(i.getQuantidade());
            if (qtdExtornado.compareTo(BigDecimal.ZERO) < 0) {
                throw new ConflictException(
                        String.format("Estoque do produto: %s insuficiente para estorno de nota.", ingrediente.getNome()));
            }

            ingrediente.setEstoque(qtdExtornado);
            ingredienteRepository.save(ingrediente);
        }
        entradaRepository.delete(entrada);
    }

    private void processarItensAdicionados(RequestEntradaNotaIngredienteDto dto, EntradaIngrediente entrada) {
        dto.itens().forEach(i -> {
            Ingrediente ingrediente = ingredienteRepository.findByCodigo(i.codigoIngrediente())
                    .orElseThrow(() -> new EntityNotFoundException("Ingrediente não encontrado"));
            if (!ingrediente.isAtivo()) {
                throw new ConflictException("Ingrediente: " + ingrediente.getNome() + " inativo");
            }
            entrada.addItemIngrediente(ingrediente, i.qtdEntrada(), i.valorCusto());
        });
    }

    private void processarItensAdicionados(RequestUpdateItensEntradaIngredienteDto dto,
                                           EntradaIngrediente entrada) {
        dto.itens().forEach(i -> {
            Ingrediente ingrediente = ingredienteRepository.findByCodigo(i.codigoIngrediente())
                    .orElseThrow(() -> new EntityNotFoundException("Ingrediente não encontrado"));
            if (!ingrediente.isAtivo()) {
                throw new ConflictException("Ingrediente: " + ingrediente.getNome() + " inativo");
            }
            entrada.addItemIngrediente(ingrediente, i.qtdEntrada(), i.valorCusto());
        });
    }

    private void atualizarPrecoEstoqueComDiferenca(EntradaIngrediente entrada,
                                                   List<EntradaIngredienteItem> itensOriginais) {
        // Agrupa itens originais por ingrediente (soma quantidades)
        Map<Ingrediente, BigDecimal> totaisOriginais = somarQuantidadePorIngredientes(itensOriginais);

        // Agrupa novos itens por ingrediente (soma quantidades)
        Map<Ingrediente, BigDecimal> totaisNovos = somarQuantidadePorIngrediente(entrada);


        // Processa todos os ingredientes envolvidos
        Set<Ingrediente> todosIngredientes = new HashSet<>();
        todosIngredientes.addAll(totaisOriginais.keySet());
        todosIngredientes.addAll(totaisNovos.keySet());

        for (Ingrediente ingrediente : todosIngredientes) {
            BigDecimal totalOriginal = totaisOriginais.getOrDefault(ingrediente, BigDecimal.ZERO);
            BigDecimal totalNovo = totaisNovos.getOrDefault(ingrediente, BigDecimal.ZERO);
            BigDecimal diferenca = totalNovo.subtract(totalOriginal);

            // Atualiza estoque
            BigDecimal novoEstoque = ingrediente.getEstoque().add(diferenca);
            if (novoEstoque.compareTo(BigDecimal.ZERO) < 0) {
                throw new ConflictException("Estoque não pode ficar negativo para: " + ingrediente.getNome());
            }
            ingrediente.setEstoque(novoEstoque);

            // Atualiza preço (pega o último custo unitário do ingrediente na lista)
            if (totalNovo.compareTo(BigDecimal.ZERO) > 0) {
                Optional<EntradaIngredienteItem> ultimoItem = entrada.getItens().stream()
                        .filter(item -> item.getIngrediente().equals(ingrediente))
                        .reduce((first, second) -> second);

                ultimoItem.ifPresent(item -> {
                    ingrediente.setPrecoCusto(item.getCustoUnitario());
                    ingrediente.setPrecoVenda(item.getCustoUnitario().multiply(BigDecimal.valueOf(1.1)));
                });
            }
        }
    }

    private static Map<Ingrediente, BigDecimal> somarQuantidadePorIngredientes(List<EntradaIngredienteItem> itensOriginais) {
        return itensOriginais.stream()
                .collect(Collectors.groupingBy(
                        EntradaIngredienteItem::getIngrediente,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                EntradaIngredienteItem::getQuantidade,
                                BigDecimal::add
                        )
                ));
    }

    private static Map<Ingrediente, BigDecimal> somarQuantidadePorIngrediente(EntradaIngrediente entrada) {
        return entrada.getItens().stream()
                .collect(Collectors.groupingBy(
                        EntradaIngredienteItem::getIngrediente,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                EntradaIngredienteItem::getQuantidade,
                                BigDecimal::add
                        )
                ));
    }

    private void atualizarPrecoEstoqueIngrediente(EntradaIngrediente entrada) {
        entrada.getItens().forEach(i -> {
            Ingrediente ingrediente = ingredienteRepository.findByCodigo(i.getIngrediente().getCodigo())
                    .orElseThrow(() -> new EntityNotFoundException("Erro em atualizar ingrediente: " +
                            i.getIngrediente().getNome()));
            ingrediente.setPrecoCusto(i.getCustoUnitario());
            ingrediente.setPrecoVenda(ingrediente.getPrecoCusto().multiply(BigDecimal.valueOf(1.1)));

            if (ingrediente.getEstoque() == null) {
                ingrediente.setEstoque(BigDecimal.ZERO.add(i.getQuantidade()));
            }else {
                ingrediente.setEstoque(ingrediente.getEstoque().add(i.getQuantidade()));
            }
        });
    }

    private ResponseEntradaNotaIngredienteDto converteObjetoParaDto(EntradaIngrediente entrada) {
        List<ResponseEntradaIngredienteItem> itens = entrada.getItens()
                .stream().map(i -> {
                    if (i.getIngrediente() == null) {
                        throw new EntityNotFoundException("Ingrediente nulo em item da nota: " + entrada.getNumeroNota());
                    }

                    return new ResponseEntradaIngredienteItem(
                            i.getIngrediente().getCodigo(), i.getQuantidade(), i.getCustoUnitario());
                }).toList();

        return new ResponseEntradaNotaIngredienteDto(
                entrada.getFornecedor().getRazaoSocial(), entrada.getDataEntrada(),
                entrada.getNumeroNota(), entrada.getSerieNota(),
                entrada.getObservacao(), itens, entrada.getValorTotal()
        );
    }

    private List<ResponseEntradaNotaIngredienteDto> converteObjetoParaDto(
            List<EntradaIngrediente> entradas) {
        return entradas.stream()
                .map(this::converteObjetoParaDto).toList();
    }
}