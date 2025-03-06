package com.example.testingLogIn.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@RequestMapping
@Controller
public class PublicController {
    @GetMapping("/login")
    public String getPage(){
        return "login";
    }
    
    @GetMapping("/signing")
    public String signPage(){
        return "signin";
    }
}
