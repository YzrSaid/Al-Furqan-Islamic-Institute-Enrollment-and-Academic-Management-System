package com.example.testingLogIn;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpStatus;
import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Autowired
    private CustomUserDetailsService userService;

    private boolean isUsernameValid(String username) {
        return userService.isUsernameValid(username);
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        String errorMessage;
        if (exception instanceof UsernameNotFoundException) {
            errorMessage = "Username does not exist."; // Specifically for missing username
        } else if (exception instanceof BadCredentialsException) {
            // Check if the username exists to determine if the password is wrong
            String username = request.getParameter("username");
            if (isUsernameValid(username)) { // Replace with your logic to check if the username exists
                errorMessage = "Incorrect password."; // Username exists, so password is wrong
            } else {
                errorMessage = "Username does not exist."; // Username doesn't exist
            }
        } else {
            errorMessage = "Authentication failed: " + exception.getMessage(); // Generic error message
        }

        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + errorMessage + "\"}");
    }
}