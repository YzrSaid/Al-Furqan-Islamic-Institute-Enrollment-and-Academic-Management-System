/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.testingLogIn.Controllers;

import com.example.testingLogIn.Models.AccountRegister;
import com.example.testingLogIn.Repositories.AccountRegisterRepo;
import com.example.testingLogIn.WebsiteSecurityConfiguration.CustomUserDetailsService;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/register")
@Controller
public class AccountRegisterController {
    AccountRegisterRepo accountRegRepo;
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    public AccountRegisterController(AccountRegisterRepo accountRegRepo,
                                     CustomUserDetailsService customUserDetailsService) {
        this.accountRegRepo = accountRegRepo;
        this.customUserDetailsService = customUserDetailsService;
    }

    @PostMapping
    public ResponseEntity<String> registerAccount(@ModelAttribute AccountRegister accountRegister){
        System.out.println(accountRegister.toString());
        if(customUserDetailsService.usernameExist(accountRegister.getUsername()))
            return new ResponseEntity<>("Email Already Used",HttpStatus.CONFLICT);
        else{
            accountRegRepo.save(accountRegister);
            return new ResponseEntity<>("Account Successfully Sent for Registration",HttpStatus.OK);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> removeDetails(@PathVariable int id){
        try{
            AccountRegister todelete=accountRegRepo.findById(id).get();
            accountRegRepo.delete(todelete);
            return new ResponseEntity<>("Account Successfully Deleted",HttpStatus.OK);
        }catch(NoSuchElementException nosee){
            return new ResponseEntity<>("Account Does Not Exist",HttpStatus.NOT_FOUND);
        }
    }
}
