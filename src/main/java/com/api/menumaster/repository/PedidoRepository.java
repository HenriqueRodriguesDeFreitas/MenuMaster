package com.api.menumaster.repository;

import com.api.menumaster.model.Pedido;
import com.api.menumaster.model.enums.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PedidoRepository extends JpaRepository<Pedido, UUID> {
    List<Pedido> findByDataEmissaoBetween(LocalDateTime inicio, LocalDateTime fim);
    List<Pedido> findByTotalPedidoBetween(BigDecimal inicio, BigDecimal fim);
    List<Pedido> findByMesaOrderByDataEmissao(Integer mesa);
    List<Pedido> findByStatusPedidoOrderByDataEmissao(StatusPedido statusPedido);
}
