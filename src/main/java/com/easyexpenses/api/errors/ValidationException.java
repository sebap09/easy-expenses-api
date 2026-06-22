package com.easyexpenses.api.errors;

import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {

    private final ErrorCode errorCode;

    public ValidationException(String message, ErrorCode errorCode){
        super(message);
        this.errorCode = errorCode;
    }
}
