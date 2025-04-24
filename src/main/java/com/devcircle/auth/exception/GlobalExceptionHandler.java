package com.devcircle.auth.exception;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<?> handleDuplicate(DuplicateResourceException e) {

        return new ResponseEntity<>(
                Map.of("timestamp", LocalDateTime.now(), "status", HttpStatus.CONFLICT.value(), "error", "conflict",
                        "message", e.getMessage()),
                HttpStatus.CONFLICT);

    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> handleInvalidCredentials(InvalidCredentialsException e) {

        return new ResponseEntity<>(
                Map.of("timestamp", LocalDateTime.now(), "status", HttpStatus.UNAUTHORIZED.value(), "error",
                        "unauthorized",
                        "message", e.getMessage()),
                HttpStatus.UNAUTHORIZED);

    }

}
