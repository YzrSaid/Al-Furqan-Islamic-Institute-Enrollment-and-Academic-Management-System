package com.example.testingLogIn.Controllers;

import com.example.testingLogIn.CustomObjects.PagedResponse;
import com.example.testingLogIn.ModelDTO.StudentDTO;
import com.example.testingLogIn.Models.SchoolYear;
import com.example.testingLogIn.Repositories.StudentSubjectGradeRepo;
import com.example.testingLogIn.Services.StudentServices;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<PagedResponse> getStudentByDisplayId(
            @RequestParam(required = false) Boolean fullyPaid,
            @RequestParam(required = false,defaultValue = "") String q,
            @RequestParam(required = false,defaultValue = "") String sortBy,
            @RequestParam(required = false,defaultValue = "1") int pageNo,
            @RequestParam(required = false, defaultValue = "10") int pageSize){
        try{
            return new ResponseEntity<>(studentService.getStudentByNameOrDisplayId(q,sortBy,pageNo,pageSize,fullyPaid),HttpStatus.OK);
        }catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/find-student/name/{studentName}")
    public ResponseEntity<StudentDTO> getStudentByName(@PathVariable String studentName){
        try {
            return new ResponseEntity<>(studentService.getStudentBtName(studentName), HttpStatus.OK);
        }catch (NullPointerException npe){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/find-student/{studentId}")
    public ResponseEntity<StudentDTO> getStudent(@PathVariable int studentId){
        try {
            return new ResponseEntity<>(studentService.getStudent(studentId), HttpStatus.OK);
        }catch (NullPointerException npe){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
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
            npe.printStackTrace();
            return new ResponseEntity<>("Selected Grade and Section Not Found",HttpStatus.NOT_FOUND);
        }catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Transaction Failed",HttpStatus.CONFLICT);
        }
    }
    
    @PutMapping("/update/{studentId}")
    public ResponseEntity<String> updateStudent(@PathVariable int studentId, @RequestBody StudentDTO student){
        return new ResponseEntity<>(studentService.updateStudent(studentId, student),HttpStatus.OK);
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
