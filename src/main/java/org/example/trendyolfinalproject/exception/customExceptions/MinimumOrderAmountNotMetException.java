package org.example.trendyolfinalproject.exception.customExceptions;

public class MinimumOrderAmountNotMetException extends RuntimeException {
    public MinimumOrderAmountNotMetException(String message) {
        super(message);
    }
}
