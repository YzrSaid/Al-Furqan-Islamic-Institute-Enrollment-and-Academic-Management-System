package com.example.testingLogIn.Controllers;

import com.example.testingLogIn.ModelDTO.TeacherDTO;
import com.example.testingLogIn.Models.Teacher;
import com.example.testingLogIn.Services.TeacherServices;
import com.example.testingLogIn.WebsiteSecurityConfiguration.CustomUserDetailsService;
import com.example.testingLogIn.WebsiteSecurityConfiguration.UserModel;
import com.example.testingLogIn.WebsiteSecurityConfiguration.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/teachers")
@Controller
public class TeacherController {
    
    private TeacherServices teacherServices;
    private CustomUserDetailsService userService;
    private UserRepo userRepo;
    @Autowired
    public TeacherController(TeacherServices teacherServices, CustomUserDetailsService userService,UserRepo userRepo) {
        this.teacherServices = teacherServices;
        this.userService = userService;
        this.userRepo=userRepo;
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<Teacher>> getAllTeachers(){
        return new ResponseEntity<>(teacherServices.getTeacherList(), HttpStatus.OK);
    }

    @PostMapping("/add/{staffId}")
    public ResponseEntity<String> addTeacherRecord(@RequestBody @Validated TeacherDTO teacher, @PathVariable int staffId){
        Teacher teacherBuild = Teacher.builder()
                          .user(userService.getuser(staffId))
                          .address(teacher.getAddress())
                          .birthdate(teacher.getBirthdate())
                          .gender(teacher.getGender())
                          .contactNum(teacher.getContactNum())
                          .build();

        return new ResponseEntity<>(teacherBuild.toString(),HttpStatus.OK);
    }

    @GetMapping("/unregistered")
    public ResponseEntity<List<UserModel>> getUnregistered(){
        return new ResponseEntity<>(teacherServices.unverifiedTeacherAccounts(),HttpStatus.OK);
    }
}
