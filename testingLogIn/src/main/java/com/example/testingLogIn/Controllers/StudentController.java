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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    
    @GetMapping("/{status}")
    public ResponseEntity<List<StudentDTO>> getAllStudents(@PathVariable String status){
        if(status.equalsIgnoreCase("new"))
            return new ResponseEntity<>(studentService.getNewStudents(),HttpStatus.OK);
        else if(status.equalsIgnoreCase("old"))
            return new ResponseEntity<>(studentService.getOldStudents(),HttpStatus.OK);
        else
            return new ResponseEntity<>(studentService.getTransfereeStudents(),HttpStatus.OK);
    }
 
    @PostMapping("/add")
    public ResponseEntity<String> addStudents(@RequestBody StudentDTO students){
        try{
            if(studentService.addStudent(students)){
                return new ResponseEntity<>("New Student Added Successfully",HttpStatus.OK);}
            else
                return new ResponseEntity<>("Student Name Already Exist",HttpStatus.NOT_ACCEPTABLE);
        }catch(NullPointerException npe){
            return new ResponseEntity<>("Selected Grade and Section Not Found",HttpStatus.NOT_FOUND);
        }catch(Exception e){
            return new ResponseEntity<>("Transaction Failed",HttpStatus.CONFLICT);
        }
    }
    
    @PutMapping("/update")
    public ResponseEntity<String> updateStudent(@RequestBody StudentDTO student){
        try{
            if(!studentService.updateStudent(student))
                return new ResponseEntity<>("Student Record Not Found",HttpStatus.NOT_FOUND);
            else
                return new ResponseEntity<>("Student Name Already Exist",HttpStatus.OK);
        }catch(NullPointerException npe){
            return new ResponseEntity<>("Student Record Not Found",HttpStatus.NOT_FOUND);
        }catch(Exception e){
            return new ResponseEntity<>("Transaction Failed",HttpStatus.CONFLICT);
        }
    }
    
    @DeleteMapping("/delete/{studentNumber}")
    public ResponseEntity<String> deleteStudentRecord(@PathVariable int studentNumber){
        try{
            if(studentService.deleteStudent(studentNumber))
                return new ResponseEntity<>("Student Record Deleted Successfully",HttpStatus.OK);
            else
                return new ResponseEntity<>("Student Record Not Found",HttpStatus.NOT_FOUND);
        }catch(Exception e){
            return new ResponseEntity<>("Transaction Failed",HttpStatus.CONFLICT);
        }
    }
    
}
