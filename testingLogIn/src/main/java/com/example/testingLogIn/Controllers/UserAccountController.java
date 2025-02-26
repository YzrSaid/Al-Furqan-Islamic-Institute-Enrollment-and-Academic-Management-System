/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.testingLogIn.Controllers;

import com.example.testingLogIn.Models.AccountRegister;
import com.example.testingLogIn.WebsiteSecurityConfiguration.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.testingLogIn.ModelDTO.UserDTO;
import java.util.List;

@RequestMapping("/user")
@Controller
public class UserAccountController {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return new ResponseEntity<>(customUserDetailsService.getAllUsers(), HttpStatus.OK);}
    
    @PostMapping("/register")
    public ResponseEntity<String> registerAccount(@RequestBody AccountRegister toRegister){
        if(customUserDetailsService.usernameExist(toRegister.getUsername()))
            return new ResponseEntity<>("Email Already Taken",HttpStatus.CONFLICT);
        else{
            customUserDetailsService.registerNewUser(toRegister);
            return new ResponseEntity<>("Account Registered Successfully",HttpStatus.OK);
        }
    }
    
    @PutMapping("/restrict/{id}")
    public ResponseEntity<String> restrictUserAccount(@PathVariable int id){
        if(customUserDetailsService.restrictUser(id))
            return new ResponseEntity<>("Account Restricted Successfully",HttpStatus.OK);
        else
            return new ResponseEntity<>("Process Failed",HttpStatus.CONFLICT);
    }

    @PutMapping("/unrestrict/{id}")
    public ResponseEntity<String> unrestrictUserAccount(@PathVariable int id) {
        if (customUserDetailsService.unrestrictUser(id))
            return new ResponseEntity<>("Account Unrestricted Successfully", HttpStatus.OK);
        else
            return new ResponseEntity<>("Process Failed", HttpStatus.CONFLICT);
    }
}
