package org.example.trendyolfinalproject.exception.customExceptions;

public class CouponUsageLimitExceededException extends RuntimeException {
    public CouponUsageLimitExceededException(String message) {
        super(message);
    }
}
