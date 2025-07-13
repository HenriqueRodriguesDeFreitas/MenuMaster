package com.api.menumaster.exception.custom;

public class ConflictEntityException extends RuntimeException {
    public ConflictEntityException(String message) {
        super(message);
    }
}
