package com.example.testingLogIn.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.security.InvalidKeyException;

@ControllerAdvice
public class ErrorHandlers {
    @ExceptionHandler(InvalidKeyException.class)
    public ResponseEntity<String> invalidKeyHandler(InvalidKeyException ike){
        return new ResponseEntity<>(ike.getMessage(), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> notFoundHandler(NullPointerException npe){
        return new ResponseEntity<>(npe.getMessage(),HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exceptionHandler(Exception e){
        return new ResponseEntity<>("Server Error",HttpStatus.CONFLICT);
    }
}
