package com.belvinard.userManagement.exceptions;

import org.springframework.http.HttpStatus;

public class CustomUsernameNotFoundException extends RuntimeException {
    private final HttpStatus status;

    public CustomUsernameNotFoundException(String message) {
        super(message);
        this.status = HttpStatus.UNAUTHORIZED;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
