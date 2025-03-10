package com.example.testingLogIn.Controllers;

import com.example.testingLogIn.Models.SchoolYearSemester;
import com.example.testingLogIn.Services.sySemesterServices;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author magno
 */
@RequestMapping("/semester")
@Controller
public class SemesterController {
    
    private final sySemesterServices semService;

    @Autowired
    public SemesterController(sySemesterServices semService) {
        this.semService = semService;
    }
    
    
    @GetMapping("/all")
    public ResponseEntity<Map<String,List<SchoolYearSemester>>> getAllSemester(){
        Map<String,List<SchoolYearSemester>> semesters = semService.getAllSemesters();
        if(semesters.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<>(semesters,HttpStatus.OK);
    }
    
    @GetMapping("/currentactive")
    public ResponseEntity<SchoolYearSemester> getCurrentSem(){
        return new ResponseEntity<>(semService.getCurrentActive(),HttpStatus.OK);
    }
    
    @PutMapping("/activate/{semNumber}")
    public ResponseEntity<String> activateSemester(@PathVariable int semNumber){
        if(semService.activateSemester(semNumber))
            return new ResponseEntity<>("Semester Activated Successfully",HttpStatus.OK);
        else
            return new ResponseEntity<>("Semester Not Found",HttpStatus.NOT_FOUND);
    }
    
    @PutMapping("/deactivate/{semNumber}")
    public ResponseEntity<String> deactivateSemester(@PathVariable int semNumber){
        if(semService.deactivateSemester(semNumber))
            return new ResponseEntity<>("Semester Deactivated Successfully",HttpStatus.OK);
        else
            return new ResponseEntity<>("Semester Not Found",HttpStatus.NOT_FOUND);
    }
    @PutMapping("/finish/{semNumber}")
    public ResponseEntity<String> finishSemester(@PathVariable int semNumber){
        if(semService.finishSemester(semNumber))
            return new ResponseEntity<>("Semester Activated Successfully",HttpStatus.OK);
        else
            return new ResponseEntity<>("Semester Not Found",HttpStatus.NOT_FOUND);
    }
}
