package com.api.menumaster.mappper;

import com.api.menumaster.dtos.response.ResponseIngredienteProdutoDto;
import com.api.menumaster.dtos.response.ResponseProdutoDto;
import com.api.menumaster.model.Produto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProdutoMapper {

    default ResponseProdutoDto toResponse(Produto produto) {

        List<ResponseIngredienteProdutoDto> responses = produto.getIngredientesAssociados()
                .stream().map(p -> new ResponseIngredienteProdutoDto(p.getIngrediente().getNome(), p.getQuantidade())).toList();

        return new ResponseProdutoDto(produto.getNome(), produto.getCodigoProduto(), produto.getDescricao(), produto.getPrecoCusto(), produto.getPrecoVenda(), produto.isAtivo(), responses);

    }
}
