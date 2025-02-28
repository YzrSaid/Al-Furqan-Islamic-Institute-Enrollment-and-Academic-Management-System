package com.example.testingLogIn.Controllers;

import com.example.testingLogIn.ModelDTO.StudentDTO;
import com.example.testingLogIn.Models.Student;
import com.example.testingLogIn.Services.StudentServices;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author magno
 */
@RequestMapping("/students")
@Controller
public class StudentController {
    
    @Autowired
    private StudentServices studentService;
    
    @GetMapping("/all")
    public ResponseEntity<List<StudentDTO>> getAllStudents(){
        return new ResponseEntity<>(studentService.getAllStudent(),HttpStatus.OK);
    }
 
    @PostMapping("/add")
    public ResponseEntity<String> addStudents(@RequestBody StudentDTO students){
        try{
            if(studentService.addStudent(students))
                return new ResponseEntity<>("New Student Added Successfully",HttpStatus.OK);
            else
                return new ResponseEntity<>("Student Name Already Exist",HttpStatus.NOT_ACCEPTABLE);
        }catch(NullPointerException npe){
            return new ResponseEntity<>("Selected Grade and Section Not Found",HttpStatus.NOT_FOUND);
        }catch(Exception e){
            return new ResponseEntity<>("Transaction Failed",HttpStatus.CONFLICT);
        }
    }
    
}
