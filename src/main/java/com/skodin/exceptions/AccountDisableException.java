package com.skodin.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AccountDisableException extends RuntimeException {
    public AccountDisableException(String message) {
        super(message);
    }
}
