package com.api.menumaster.mappper;

import com.api.menumaster.dtos.request.RequestMovimentoTesourariaDto;
import com.api.menumaster.dtos.response.ResponseTesourariaMovimentoDto;
import com.api.menumaster.model.TesourariaMovimento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TesourariaMovimentoMapper {

    @Mapping(target = "idMovimento", source = "movimento.id")
    @Mapping(target = "usuarioMovimento", source = "movimento.usuario")
    @Mapping(target = "dataMovimento", source = "movimento.dataMovimentacao")
    @Mapping(target = "descricao", source = "movimento.descricao")
    ResponseTesourariaMovimentoDto toResponse(TesourariaMovimento movimento);

}
