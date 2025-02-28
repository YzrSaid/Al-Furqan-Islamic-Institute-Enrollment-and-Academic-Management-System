/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.testingLogIn.Controllers;

import com.example.testingLogIn.Models.SchoolYearSemester;
import com.example.testingLogIn.Services.sySemesterServices;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<List<SchoolYearSemester>> getAllSemester(){
        return new ResponseEntity<>(semService.getAllSemesters(),HttpStatus.OK);
    }
}
