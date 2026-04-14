package com.kaarigar.backend.exception;

// 🛡️ Concept: Custom Unchecked Exception
public class MarketplaceException extends RuntimeException {
    public MarketplaceException(String message) {
        super(message);
    }
}