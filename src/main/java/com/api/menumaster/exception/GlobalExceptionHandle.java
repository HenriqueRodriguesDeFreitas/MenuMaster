package com.api.menumaster.exception;

import com.api.menumaster.dtos.response.ResponseErroDto;
import com.api.menumaster.exception.custom.ConflictException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandle {

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ResponseErroDto> handleConflictException(ConflictException e) {
        ResponseErroDto response = new ResponseErroDto(LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "Erro de conflito",
                e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}
