package com.example.testingLogIn.WebsiteSecurityConfiguration;

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
import org.springframework.security.authentication.AccountStatusException;

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
            errorMessage = "Username does not exist.";
        } else if (exception instanceof BadCredentialsException) {
            String username = request.getParameter("username");
            if (isUsernameValid(username)) {
                errorMessage = "Incorrect password.";
            } else {
                errorMessage = "Username does not exist.";
            }
        } else if(exception instanceof AccountStatusException){
            errorMessage = exception.getMessage();
        }else {
            errorMessage = exception.getMessage();
        }

        response.setContentType("application/json");
        response.getWriter().write(errorMessage);
    }
}