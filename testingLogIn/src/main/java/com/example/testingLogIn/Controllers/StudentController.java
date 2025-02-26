package com.example.testingLogIn.Controllers;

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
    public ResponseEntity<List<Student>> getAllStudents(){
        return new ResponseEntity<>(studentService.getAllStudent(),HttpStatus.OK);
    }
 
    @PostMapping("/add")
    public ResponseEntity<Map<String,List<Student>>> addStudents(@RequestBody List<Student> students){
        try{
            Map<String,List<Student>> rejectedForms = studentService.addStudent(students);
        if(rejectedForms.isEmpty())
            return new ResponseEntity<>(HttpStatus.OK);
        else
            return new ResponseEntity<>(rejectedForms,HttpStatus.CONFLICT);
        }catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
    
}
