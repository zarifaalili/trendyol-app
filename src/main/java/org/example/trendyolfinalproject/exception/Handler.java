package org.example.trendyolfinalproject.exception;

import org.example.trendyolfinalproject.exception.customExceptions.*;
import org.example.trendyolfinalproject.exception.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class Handler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Response> handle(RuntimeException e) {
        return ResponseEntity
                .badRequest()
                .body(new Response(e.getMessage(), "400"));


        }


    @ExceptionHandler(VerifyEmailException.class)
    public ResponseEntity<Response> handle(VerifyEmailException e) {
        return ResponseEntity
                .accepted()
                .body(new Response(e.getMessage(), "409"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handle(Exception e) {
        return ResponseEntity
                .badRequest()
                .body(new Response(e.getMessage(), HttpStatus.BAD_REQUEST.toString()));

    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Response> handle(EmailAlreadyExistsException e) {
        return ResponseEntity
                .badRequest()
                .body(new Response(e.getMessage(), HttpStatus.BAD_REQUEST.toString()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Response> handle(NotFoundException e) {
        return ResponseEntity
                .badRequest()
                .body(new Response(e.getMessage(), HttpStatus.BAD_REQUEST.toString()));
    }

    @ExceptionHandler(CouponUsageLimitExceededException.class)
    public ResponseEntity<Response> handle(CouponUsageLimitExceededException e) {
        return ResponseEntity
                .badRequest()
                .body(new Response(e.getMessage(), HttpStatus.BAD_REQUEST.toString()));
    }

    @ExceptionHandler(MinimumOrderAmountNotMetException.class)
    public ResponseEntity<Response> handle(MinimumOrderAmountNotMetException e) {
        return ResponseEntity
                .badRequest()
                .body(new Response(e.getMessage(), HttpStatus.BAD_REQUEST.toString()));
    }

    @ExceptionHandler(AlreadyException.class)
    public ResponseEntity<Response> handle(AlreadyException e) {
        return ResponseEntity
                .badRequest()
                .body(new Response(e.getMessage(), HttpStatus.BAD_REQUEST.toString()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}

