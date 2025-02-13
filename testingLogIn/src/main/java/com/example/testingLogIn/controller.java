package com.example.testingLogIn;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequestMapping
@Controller
public class controller {

    @Autowired
    UserRepo userRepo;

    @GetMapping("/token")
    public ResponseEntity<CsrfToken> getToken(HttpServletRequest request){
        return new ResponseEntity<>((CsrfToken) request.getAttribute("_csrf"),HttpStatus.OK);
    }

    @GetMapping("/login")
    public String getPage(){
        return "LogIn";
    }


    @GetMapping("/newpage")
    public String newPage(HttpServletRequest request){
        UserModel model = userRepo.findByUsername(request.getUserPrincipal().getName());
        System.out.println(model.getUsername());
        return "newpage";
    }

    @GetMapping("/signin")
    public String signPage(){
        return "signin";
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerMe(){
        System.out.println("Someone registered");
        return new ResponseEntity<>("Doneee",HttpStatus.OK);
    }
//    @PostMapping("/login")
//    public ResponseEntity<String> doesExist(@RequestBody UserModel user){
//        if(user.username.equals("Clifford")){
//            if(!user.password.equals("123"))
//                return new ResponseEntity<>("Wrong Password", HttpStatus.NOT_ACCEPTABLE);
//            return new ResponseEntity<>("User Exist",HttpStatus.FOUND);
//        }
//
//        return new ResponseEntity<>("User Email Does Not Exist",HttpStatus.NOT_FOUND);
//    }
}
