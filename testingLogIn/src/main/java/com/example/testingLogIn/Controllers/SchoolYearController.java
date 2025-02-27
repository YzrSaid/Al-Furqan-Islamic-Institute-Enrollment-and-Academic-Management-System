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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
        try{
            int from = Integer.parseInt(sy.getSchoolYear().substring(0, 4));
            int to = Integer.parseInt(sy.getSchoolYear().substring(5, 9));
            if((to-from) != 1)
                return new ResponseEntity<>("Invalid School Year",HttpStatus.NOT_ACCEPTABLE);
            else if(!schoolYearService.addNewSchoolYear(sy))
                return new ResponseEntity<>("School Year Already Exist",HttpStatus.NOT_ACCEPTABLE);
            else
                return new ResponseEntity<>("School Year Successfully Added",HttpStatus.OK);
        }catch(NumberFormatException e){
            return new ResponseEntity<>("School Year Contains Letter/s",HttpStatus.NOT_ACCEPTABLE);
        }catch(Exception e){
            return new ResponseEntity<>("Process Failed",HttpStatus.CONFLICT);
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<SchoolYear>> getAllSchoolYear(){
        try{
        return new ResponseEntity<>(schoolYearService.getSchoolYears(),HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
    
    @PutMapping("/update")
    public ResponseEntity<String> updateSchoolYear(@ModelAttribute SchoolYear sy){
        try{
            int from = Integer.parseInt(sy.getSchoolYear().substring(0, 4));
            int to = Integer.parseInt(sy.getSchoolYear().substring(5, 9));
            if((to-from) != 1)
                return new ResponseEntity<>("Invalid School Year",HttpStatus.NOT_ACCEPTABLE);
            else if(!schoolYearService.updateSchoolYear(sy))
                return new ResponseEntity<>("School Year Already Exist",HttpStatus.NOT_ACCEPTABLE);
            else
                return new ResponseEntity<>("School Year Successfully Updated",HttpStatus.OK);
        }catch(NullPointerException npe){
            return new ResponseEntity<>("School Year Not Found",HttpStatus.NOT_FOUND);
        }catch(NumberFormatException e){
            return new ResponseEntity<>("School Year Contains Letter/s",HttpStatus.NOT_ACCEPTABLE);
        }catch(Exception e){
            return new ResponseEntity<>("Process Failed",HttpStatus.CONFLICT);
        }
    }
    
    @DeleteMapping("/delete/{syNum}")
    public ResponseEntity<String> deleteSchoolYear(@PathVariable int syNum){
        if(schoolYearService.deleteSchoolYear(syNum))
            return new ResponseEntity<>("School Year Deleted Updated",HttpStatus.OK);
        else
            return new ResponseEntity<>("School Year Not Found",HttpStatus.NOT_FOUND);
    }
}
