package com.api.menumaster.mappper;

import com.api.menumaster.dtos.response.ResponseTesourariaDto;
import com.api.menumaster.model.Tesouraria;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TesourariaMapper {

    @Mapping(target = "id", source = "tesouraria.id")
    @Mapping(target = "dataAbertura", source = "tesouraria.dataAbertura")
    @Mapping(target = "dataFechamento", source = "tesouraria.dataFechamento")
    @Mapping(target = "dataReabertura", source = "tesouraria.dataReabertura")
    @Mapping(target = "saldoInicial", source = "tesouraria.saldoInicial")
    @Mapping(target = "saldoFinal", source = "tesouraria.saldoFinal")
    @Mapping(target = "usuarioAbertura", source = "tesouraria.usuarioAbertura")
    @Mapping(target = "usuarioFechamento", source = "tesouraria.usuarioFechamento")
    @Mapping(target = "usuarioReabertura", source = "tesouraria.usuarioReabertura")
    ResponseTesourariaDto toResponse(Tesouraria tesouraria);
}
