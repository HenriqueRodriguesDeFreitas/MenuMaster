package com.api.menumaster.mappper;

import com.api.menumaster.dtos.response.ResponseItemPedidoDto;
import com.api.menumaster.model.ItemPedido;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemPedidoMapper {

    @Mapping(target = "nomeProduto", source = "produto.nome")
    @Mapping(target = "qtdProduto", source = "quantidadeProduto")
    @Mapping(target = "precoUnitario", source = "produto.precoVenda")
    ResponseItemPedidoDto toResponse(ItemPedido itemPedido);
}
