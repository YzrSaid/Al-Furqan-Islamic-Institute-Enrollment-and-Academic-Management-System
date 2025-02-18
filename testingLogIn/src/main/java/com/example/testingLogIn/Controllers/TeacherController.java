package com.example.testingLogIn.Controllers;

import com.example.testingLogIn.ModelDTO.TeacherDTO;
import com.example.testingLogIn.ModelDTO.UserDTO;
import com.example.testingLogIn.Services.TeacherServices;
import com.example.testingLogIn.WebsiteSecurityConfiguration.CustomUserDetailsService;
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
    
    @DeleteMapping("/delete/{staffId}")
    public ResponseEntity<String> deleteTeacherRecord(@PathVariable int staffId){
        try{
            if(teacherServices.deleteTeacherRecord(staffId))
                return new ResponseEntity<>("Teacher Record Deleted Successfully",HttpStatus.OK);
            else
                return new ResponseEntity<>("Teacher ID Not Found",HttpStatus.NOT_FOUND);
        }catch(Exception e){
            return new ResponseEntity<>("Process Failed",HttpStatus.CONFLICT);
        }
    }
    
    @GetMapping("/registered")
    public ResponseEntity<List<TeacherDTO>> getUnregisteredTeachers(){
        try{
            return new ResponseEntity<>(teacherServices.getTeacherList(), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/add/{staffId}")
    public ResponseEntity<String> addTeacherRecord(@RequestBody @Validated TeacherDTO teacher, @PathVariable int staffId){
        System.out.println("Im here");
        try{
            if(teacherServices.addNewTeacherInfo(teacher, staffId)){
                return new ResponseEntity<>("New Teacher Info Added Successfully",HttpStatus.OK);}
            else
                return new ResponseEntity<>("Teacher Record Already Exist",HttpStatus.BAD_REQUEST);
        }catch(MatchException me){
            return new ResponseEntity<>("Staff ID Not Found",HttpStatus.NOT_FOUND);
        }
        catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Process Failed",HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/unregistered")
    public ResponseEntity<List<UserDTO>> getUnregistered(){
        try{
        return new ResponseEntity<>(teacherServices.notRegisteredTeacherAccounts(),HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update/{staffid}")
    public ResponseEntity<String> updateTeacher(@RequestBody TeacherDTO teacherDTO,@PathVariable int staffid){
        try{
            if(teacherServices.updateTeacherInfo(teacherDTO, staffid))
                return new ResponseEntity<>("Teacher Info Updated Successfully",HttpStatus.OK);
            else
                return new ResponseEntity<>("Teacher Record is Not Found",HttpStatus.NOT_FOUND);
        }catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Process Failed",HttpStatus.CONFLICT);
        }
    }
}
