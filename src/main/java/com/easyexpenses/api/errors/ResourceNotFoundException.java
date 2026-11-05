package com.easyexpenses.api.errors;

public class ResourceNotFoundException extends RuntimeException {
    private final ErrorCode errorCode;

    public ResourceNotFoundException(String message, ErrorCode errorCode){
        super(message);
        this.errorCode = errorCode;
    }
}