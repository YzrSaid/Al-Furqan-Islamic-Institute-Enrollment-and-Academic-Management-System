package com.example.testingLogIn.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@RequestMapping
@Controller
public class PublicController {
    @GetMapping("/")
    public String getPage(){
        return "login";
    }
    

    @GetMapping("/forgot-password")
    public String forgotPassword(){
        return "password-reset/forgot-password";
    }
    @GetMapping("/reset-password/{token}")
    public String resetPassword(){
        return "password-reset/reset-password";
    }

    @GetMapping("/account-confirmation/{token}")
    public String accountConfirmationPage(){
        return "password-reset/account-confirmation";
    }
}
