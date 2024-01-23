package ru.practicum.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ValidationException extends RuntimeException{
    public ValidationException() {
    }

    public ValidationException(String message) {
        super(message);
    }
}
