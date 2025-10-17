package org.example.trendyolfinalproject.exception;

import org.example.trendyolfinalproject.exception.customExceptions.*;
import org.example.trendyolfinalproject.exception.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class Handler {


    @ExceptionHandler(VerifyEmailException.class)
    public ResponseEntity<Response> handle(VerifyEmailException e) {
        Response response = new Response(e.getMessage(), "409");
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Response> handle(BadCredentialsException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new Response(e.getMessage(), "401"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Response> handle(AccessDeniedException e) {
        Response response = new Response(e.getMessage(), "403");
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Response> handle(EmailAlreadyExistsException e) {
        return ResponseEntity
                .badRequest()
                .body(new Response(e.getMessage(), HttpStatus.CONFLICT.toString()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Response> handle(NotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new Response(e.getMessage(), HttpStatus.NOT_FOUND.toString()));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Response> handle(ResponseStatusException e) {
        return ResponseEntity
                .status(e.getStatusCode())
                .body(new Response(e.getReason(), String.valueOf(e.getStatusCode().value())));
    }


    @ExceptionHandler(CouponUsageLimitExceededException.class)
    public ResponseEntity<Response> handle(CouponUsageLimitExceededException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new Response(e.getMessage(), HttpStatus.CONFLICT.toString()));
    }

    @ExceptionHandler(MinimumOrderAmountNotMetException.class)
    public ResponseEntity<Response> handle(MinimumOrderAmountNotMetException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new Response(e.getMessage(), HttpStatus.CONFLICT.toString()));
    }

    @ExceptionHandler(AlreadyException.class)
    public ResponseEntity<Response> handle(AlreadyException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new Response(e.getMessage(), HttpStatus.CONFLICT.toString()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Response> handle(RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new Response(e.getMessage(), "409"));


    }


//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<Response> handle(Exception e) {
//        return ResponseEntity
//                .badRequest()
//                .body(new Response(e.getMessage(), HttpStatus.BAD_REQUEST.toString()));
//
//    }

}

