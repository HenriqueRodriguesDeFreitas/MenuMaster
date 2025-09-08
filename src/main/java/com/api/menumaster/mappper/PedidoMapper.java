package com.api.menumaster.mappper;

import com.api.menumaster.dtos.response.ResponsePedidoDto;
import com.api.menumaster.model.Pedido;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ItemPedidoMapper.class})
public interface PedidoMapper {

    @Mapping(target = "itens", source = "itensAssociados")
    ResponsePedidoDto toResponse(Pedido pedido);

}
