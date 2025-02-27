/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.testingLogIn.Controllers;

import com.example.testingLogIn.Models.SchoolYear;
import com.example.testingLogIn.Services.SchoolYearServices;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author magno
 */
@RequestMapping("/schoolyear")
@Controller
public class SchoolYearController {
    @Autowired
    private SchoolYearServices schoolYearService;
    
    @PostMapping("/add")
    public ResponseEntity<String> addNewSchoolYear(@RequestBody SchoolYear sy){
        int from = Integer.parseInt(sy.getSchoolYear().substring(0, 4));
        int to = Integer.parseInt(sy.getSchoolYear().substring(5, 9));
        
        if((to-from) != 1)
            return new ResponseEntity<>("Invalid School Year",HttpStatus.NOT_ACCEPTABLE);
        else if(!schoolYearService.addNewSchoolYear(sy))
            return new ResponseEntity<>("School Year Already Exist",HttpStatus.NOT_ACCEPTABLE);
        else
            return new ResponseEntity<>("School Year Successfully Added",HttpStatus.OK);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<SchoolYear>> getAllSchoolYear(){
        try{
        return new ResponseEntity<>(schoolYearService.getSchoolYears(),HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
    
    @PutMapping("/activate")
    public ResponseEntity<String> activateSchoolYear(@RequestBody SchoolYear sy){
        try{
            if(schoolYearService.activateSY(sy.getSchoolYearNum())==1)
                return new ResponseEntity<>("First Semester Activated",HttpStatus.OK);
            else
                return new ResponseEntity<>("Second Semester Activated",HttpStatus.OK);
        }catch(NullPointerException npe){
            return new ResponseEntity<>("School Yeat Not Found",HttpStatus.NOT_FOUND);
        }catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Process Failed",HttpStatus.CONFLICT);
        }
    }
}
