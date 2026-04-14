package com.kaarigar.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice // 🛡️ Concept: Aop (Aspect Oriented Programming) - intercepts all controllers
public class GlobalExceptionHandler {

    @ExceptionHandler(MarketplaceException.class)
    public ResponseEntity<?> handleMarketplaceException(MarketplaceException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", "error");
        body.put("message", ex.getMessage());

        // We return a 400 (Bad Request) because the user did something wrong,
        // but the server didn't actually "break."
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}