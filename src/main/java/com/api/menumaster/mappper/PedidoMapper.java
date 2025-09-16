package com.api.menumaster.mappper;

import com.api.menumaster.dtos.response.ResponsePedidoDto;
import com.api.menumaster.model.Pedido;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ItemPedidoMapper.class})
public interface PedidoMapper {

    @Mapping(target = "itens", source = "itensAssociados")
    @Mapping(target = "status", source = "statusPedido")
    @Mapping(target = "emissao", source = "dataEmissao")
    @Mapping(target = "editado", source = "dataEdicao")
    ResponsePedidoDto toResponse(Pedido pedido);

}
