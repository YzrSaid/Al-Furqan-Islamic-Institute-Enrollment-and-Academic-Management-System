package com.example.testingLogIn.Controllers;

import com.example.testingLogIn.Enums.RegistrationStatus;
import com.example.testingLogIn.ModelDTO.StudentDTO;
import com.example.testingLogIn.Models.AccountRegister;
import com.example.testingLogIn.CustomObjects.PagedResponse;
import com.example.testingLogIn.WebsiteSecurityConfiguration.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.testingLogIn.ModelDTO.UserDTO;
import com.example.testingLogIn.Services.AccountRegisterServices;
import java.util.List;

@RequestMapping("/user")
@Controller
public class UserAccountController {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private AccountRegisterServices accountRegisterService;

    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return new ResponseEntity<>(customUserDetailsService.getAllUsers(), HttpStatus.OK);}
    
    @GetMapping("/teachers")
    public ResponseEntity<List<UserDTO>> getAllTeachers() {
        return new ResponseEntity<>(customUserDetailsService.getTeachersAccount(), HttpStatus.OK);}
    
    @GetMapping("/current-logged-in")
    public ResponseEntity<UserDTO> getCurrentUserDTO(){
        return new ResponseEntity<>(customUserDetailsService.getCurrentlyLoggedInUserDTO(),HttpStatus.OK);
    }

    @GetMapping("/student-accounts")
    public ResponseEntity<PagedResponse> getStudentAccounts(@RequestParam(defaultValue = "1",required = false) int pageNo,
                                                            @RequestParam(defaultValue = "10",required = false) int pageSize,
                                                            @RequestParam(defaultValue = "") String q){
        try{
            return new ResponseEntity<>(customUserDetailsService.getStudentAccounts(pageNo,pageSize,q),HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
    //get the currently logged student info
    @GetMapping("/student/my-info")
    public ResponseEntity<StudentDTO> getLoggedStudentInfo(){
        try{
            return new ResponseEntity<>(customUserDetailsService.getCurrentlyLoggedInStudent(), HttpStatus.OK);
        }catch (NullPointerException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<String> registerAccount(@RequestBody AccountRegister toRegister){
        if(customUserDetailsService.usernameExist(toRegister.getUsername()))
            return new ResponseEntity<>("Email Already Taken",HttpStatus.CONFLICT);
        else{
            customUserDetailsService.registerNewUser(toRegister);
            toRegister.setStatus(RegistrationStatus.APPROVED);
            accountRegisterService.registerAccount(toRegister);
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
