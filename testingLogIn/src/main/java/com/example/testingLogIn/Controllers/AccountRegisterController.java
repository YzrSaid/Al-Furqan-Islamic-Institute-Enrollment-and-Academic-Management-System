package com.example.testingLogIn.Controllers;

import com.example.testingLogIn.Enums.RegistrationStatus;
import com.example.testingLogIn.Enums.Role;
import com.example.testingLogIn.Models.AccountRegister;
import com.example.testingLogIn.Services.AccountRegisterServices;
import com.example.testingLogIn.WebsiteSecurityConfiguration.CustomUserDetailsService;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/register")
@Controller
public class AccountRegisterController {
    AccountRegisterServices accountRegisterServices;
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    public AccountRegisterController(AccountRegisterServices accountRegisterServices,
                                     CustomUserDetailsService customUserDetailsService) {
        this.accountRegisterServices = accountRegisterServices;
        this.customUserDetailsService = customUserDetailsService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> registerAccount(@ModelAttribute AccountRegister accountRegister){
        if(customUserDetailsService.usernameExist(accountRegister.getUsername()))
            return new ResponseEntity<>("Email Already Used",HttpStatus.CONFLICT);
        else{
            accountRegister.setStatus(RegistrationStatus.PENDING);
            accountRegisterServices.registerAccount(accountRegister);
            return new ResponseEntity<>("Account Successfully Sent for Registration",HttpStatus.OK);
        }
    }
    
    @GetMapping("/{status}")
    public ResponseEntity<List<AccountRegister>> getAccounts(@PathVariable String status){
        try{
            return new ResponseEntity<>(accountRegisterServices.getAccounts(status),HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(List.of(),HttpStatus.CONFLICT);
        }
    }
    
    @PutMapping("/confirm/{token}/{password}")
    public ResponseEntity<String> confirmAccountRegistration(@PathVariable String token, @PathVariable String password){
        try{
            System.out.println(token);
            System.out.println(password);
            customUserDetailsService.registerNewUser(token,password);
            return new ResponseEntity<>("Account Registration is Now Complete",HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>("Process Error",HttpStatus.CONFLICT);
        }
    }
    
    @PutMapping("/reject/{id}")
    public ResponseEntity<String> rejectAccountRegistration(@PathVariable int id){
        AccountRegister accountToConfirm = accountRegisterServices.getAccount(id);
        if(accountToConfirm == null){
            return new ResponseEntity<>("Account ID does Not Exist",HttpStatus.NOT_FOUND);
        }else{
            accountToConfirm.setStatus(RegistrationStatus.REJECTED);
            accountRegisterServices.registerAccount(accountToConfirm);
            return new ResponseEntity<>("Account Rejected Successfully",HttpStatus.OK);}
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> removeDetails(@PathVariable int id){
        try{
            accountRegisterServices.deleteAccountRegistration(id);
            return new ResponseEntity<>("Account Successfully Deleted",HttpStatus.OK);
        }catch(NoSuchElementException nosee){
            return new ResponseEntity<>("Account Does Not Exist",HttpStatus.NOT_FOUND);
        }
    }
}
