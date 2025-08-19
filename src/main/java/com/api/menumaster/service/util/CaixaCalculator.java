package com.api.menumaster.service.util;

import com.api.menumaster.model.Caixa;
import com.api.menumaster.model.CaixaMovimento;
import com.api.menumaster.model.Pedido;
import com.api.menumaster.model.enums.StatusPedido;
import com.api.menumaster.model.enums.TipoMovimento;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class CaixaCalculator {

    public BigDecimal calcularSaldoFinal(Caixa caixa) {
        return calcularValorTotalPedido(caixa.getPedidos())
                .add(calcularTotalEntradaMovimentos(caixa.getMovimentacoesCaixa())
                        .subtract(calcularTotalSaidaMovimentos(caixa.getMovimentacoesCaixa())));
    }

    public BigDecimal calcularValorTotalPedido(List<Pedido> pedidos) {
        return pedidos.stream()
                .filter(p -> p.getStatusPedido().equals(StatusPedido.FINALIZADO))
                .map(Pedido::getTotalPedido)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calcularTotalEntradaMovimentos(List<CaixaMovimento> movimentacoesCaixa) {
        return movimentacoesCaixa.stream()
                .filter(m -> m.getTipoMovimento().equals(TipoMovimento.ENTRADA))
                .map(CaixaMovimento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    public BigDecimal calcularTotalSaidaMovimentos(List<CaixaMovimento> movimentacoesCaixa) {
        return movimentacoesCaixa.stream()
                .filter(m -> m.getTipoMovimento().equals(TipoMovimento.SAIDA))
                .map(CaixaMovimento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
