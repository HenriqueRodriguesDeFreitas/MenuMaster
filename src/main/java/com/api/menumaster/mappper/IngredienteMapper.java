package com.api.menumaster.mappper;

import com.api.menumaster.dtos.response.ResponseIngredienteDto;
import com.api.menumaster.model.Ingrediente;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IngredienteMapper {

    ResponseIngredienteDto toResponse(Ingrediente ingrediente);
}
