package com.example.testingLogIn.Controllers;

import org.springframework.beans.PropertyAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.result.view.RedirectView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.thymeleaf.exceptions.TemplateInputException;

import java.security.InvalidKeyException;

@ControllerAdvice
public class ErrorHandlers {
    @ExceptionHandler(InvalidKeyException.class)
    public ResponseEntity<String> invalidKeyHandler(InvalidKeyException ike){
        return new ResponseEntity<>(ike.getMessage(), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> notFoundHandler(NullPointerException npe){
        return new ResponseEntity<>("Selected Record Not Found",HttpStatus.NOT_FOUND);
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
    public RedirectView handleNoHandlerFoundException(NoResourceFoundException nrfe,RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", "The requested resource was not found.");
        return new RedirectView("/home"); // Redirect to a custom error page
    }

    @ExceptionHandler(TemplateInputException.class)
    public RedirectView templateException(TemplateInputException nrfe, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", "The requested resource was not found.");
        return new RedirectView("/home"); // Redirect to a custom error page
    }

    @ExceptionHandler(PropertyAccessException.class)
    public ResponseEntity<String> illegalPropertyAccess(){
        return new ResponseEntity<>("Unable to access the specified property. Please ensure all necessary data is properly set and accessible.",HttpStatus.NOT_ACCEPTABLE);
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<String> exceptionHandler(Exception e){
//        return new ResponseEntity<>("Server Error",HttpStatus.CONFLICT);
//    }
}
