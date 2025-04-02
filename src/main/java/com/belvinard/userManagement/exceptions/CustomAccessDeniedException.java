package com.belvinard.userManagement.exceptions;

import org.springframework.http.HttpStatus;

public class CustomAccessDeniedException extends RuntimeException {
    private final HttpStatus status;

    public CustomAccessDeniedException(String message) {
        super(message);
        this.status = HttpStatus.FORBIDDEN;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
