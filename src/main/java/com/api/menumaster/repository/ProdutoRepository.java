package com.api.menumaster.repository;

import com.api.menumaster.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProdutoRepository extends JpaRepository<Produto, UUID> {
    Optional<Produto> findByNomeIgnoreCase(String nome);
    Optional<Produto> findByCodigoProduto(Long codigoProduto);
    List<Produto> findByNomeIgnoreCaseContaining(String nome);
    List<Produto> findByPrecoCustoBetween(BigDecimal precoInicial, BigDecimal precoFinal);
    List<Produto> findByPrecoCusto(BigDecimal precoCusto);
    List<Produto> findByPrecoVendaBetween(BigDecimal precoInicial, BigDecimal precoFinal);
    List<Produto> findByPrecoVenda(BigDecimal precoVenda);
    List<Produto> findByIsAtivoTrue();
    List<Produto> findByIsAtivoFalse();

}
