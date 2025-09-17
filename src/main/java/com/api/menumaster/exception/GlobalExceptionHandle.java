package com.api.menumaster.exception;

import com.api.menumaster.dtos.response.ResponseErroDto;
import com.api.menumaster.exception.custom.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandle {

    @ExceptionHandler(ConflictEntityException.class)
    public ResponseEntity<ResponseErroDto> handleConflictException(ConflictEntityException e) {
        ResponseErroDto response = new ResponseErroDto(LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "Erro de conflito",
                e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ResponseErroDto> handleNotFoundException(EntityNotFoundException e) {
        ResponseErroDto response = new ResponseErroDto(LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Erro de objeto não encontrada",
                e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseErroDto> handleIllegalArgumentException(IllegalArgumentException e) {
        ResponseErroDto response = new ResponseErroDto(LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Dados inválidos informados",
                e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ProdutoVinculadoAPedidoException.class)
    public ResponseEntity<ResponseErroDto> handleProdutoVinculadoAPedidoException(
            ProdutoVinculadoAPedidoException e) {
        ResponseErroDto response = new ResponseErroDto(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "Conflito: produto já vinculado a pedidos",
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(ConflictTesourariaException.class)
    public ResponseEntity<ResponseErroDto> handleConflitoTesourariaJáAbertaException(
            ConflictTesourariaException e) {
        ResponseErroDto response = new ResponseErroDto(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "Conflito: erro relacionado a tesouraria",
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(EstoqueInsuficienteException.class)
    public ResponseEntity<ResponseErroDto> handleEstoqueInsuficienteException(EstoqueInsuficienteException e) {
        ResponseErroDto response = new ResponseErroDto(
                LocalDateTime.now(),
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Entidade não processavel",
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    @ExceptionHandler(DadoPassadoNuloException.class)
    public ResponseEntity<ResponseErroDto> handleDadoNuloException(DadoPassadoNuloException e) {
        ResponseErroDto response = new ResponseErroDto(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Inserido dado vazio.",
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
