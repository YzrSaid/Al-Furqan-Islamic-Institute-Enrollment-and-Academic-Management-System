package com.example.testingLogIn.Controllers;

import com.example.testingLogIn.Models.GradeLevel;
import com.example.testingLogIn.Repositories.GradeLevelRepo;
import com.example.testingLogIn.Services.GradeLevelServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/gradelevelmaintenance")
@Controller
public class GradeLevelControllers {

    @Autowired
    GradeLevelServices gradeLevelServices;

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/all")
    public ResponseEntity<List<GradeLevel>> getAllGradeLevels(){
        return new ResponseEntity<>(gradeLevelServices.getAllGradeLevels(),HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addGradeLevel(@ModelAttribute GradeLevel gradeLevel){
        System.out.println(gradeLevel.toString());
        if(gradeLevelServices.doesLevelExist(gradeLevel.getLevelName()))
        {
            return new ResponseEntity<>("Grade Level Already Exist",HttpStatus.CONFLICT);
        }else{
            gradeLevelServices.addNewGradeLevel(gradeLevel);
            return new ResponseEntity<>("New Grade Level Has Been Added Successfulyy", HttpStatus.OK);}
    }

//    public ResponseEntity<String> updateGradeLevel(@PathVariable GradeLevel newGradeLevel){
//        gradeLevelServices.(newGradeLevel);
//        return new ResponseEntity<>("New Grade Level Has Been Updated Successfulyy", HttpStatus.OK);
//    }
}
