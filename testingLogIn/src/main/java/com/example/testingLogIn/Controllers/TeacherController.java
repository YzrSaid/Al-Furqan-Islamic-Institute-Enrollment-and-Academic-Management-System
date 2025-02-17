package com.example.testingLogIn.Controllers;

import com.example.testingLogIn.ModelDTO.TeacherDTO;
import com.example.testingLogIn.ModelDTO.UserDTO;
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

    @Autowired
    public TeacherController(TeacherServices teacherServices, CustomUserDetailsService userService) {
        this.teacherServices = teacherServices;
        this.userService = userService;
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<Teacher>> getAllTeachers(){
        return new ResponseEntity<>(teacherServices.getTeacherList(), HttpStatus.OK);
    }

    @PostMapping("/add/{staffId}")
    public ResponseEntity<String> addTeacherRecord(@RequestBody @Validated TeacherDTO teacher, @PathVariable int staffId){
        if(teacherServices.addNewTeacherInfo(teacher, staffId)){
            return new ResponseEntity<>("New Teacher Info Added Successfully",HttpStatus.OK);}
        else
            return new ResponseEntity<>("Process Failed",HttpStatus.CONFLICT);
    }

    @GetMapping("/unregistered")
    public ResponseEntity<List<UserDTO>> getUnregistered(){
        return new ResponseEntity<>(teacherServices.unverifiedTeacherAccounts(),HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateTeacher(@RequestBody TeacherDTO teacherDTO,int staffid){
        if(teacherServices.updateTeacherInfo(teacherDTO, staffid))
            return new ResponseEntity<>("Teacher Info Updated Successfully",HttpStatus.OK);
        else
            return new ResponseEntity<>("Process Failed",HttpStatus.CONFLICT);
    }
}
