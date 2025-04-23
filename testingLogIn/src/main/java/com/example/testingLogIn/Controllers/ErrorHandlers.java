package com.example.testingLogIn.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.result.view.RedirectView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> illegalArgumentHandler(IllegalArgumentException iae){
        return new ResponseEntity<>(iae.getMessage(),HttpStatus.SEE_OTHER);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public RedirectView handleNoHandlerFoundException(NoHandlerFoundException ex,
                                                      RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", "The requested resource was not found.");
        return new RedirectView("/home"); // Redirect to a custom error page
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<String> handleNoHandlerFoundException(NoResourceFoundException nrfe) {
        System.out.println("No resources found");
        return new ResponseEntity<>("No resources found",HttpStatus.NOT_FOUND); // Redirect to a custom error page
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exceptionHandler(Exception e){
        e.printStackTrace();
        return new ResponseEntity<>("Server Error",HttpStatus.CONFLICT);
    }
}
