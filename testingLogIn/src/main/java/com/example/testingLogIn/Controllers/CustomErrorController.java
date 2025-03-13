package com.example.testingLogIn.Controllers;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class CustomErrorController {

    @ExceptionHandler(NoResourceFoundException.class)
    public Mono<String> handleNoResourceFoundException(NoResourceFoundException ex, ServerWebExchange exchange) throws IllegalStateException {
        exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
        return Mono.just("error"); // Return the name of the error view or a custom message
    }

    @ExceptionHandler(Exception.class)
    public Mono<String> handleGenericException(Exception ex, ServerWebExchange exchange) throws IllegalStateException {
        exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return Mono.just("error"); // Return the name of the error view or a custom message
    }
}