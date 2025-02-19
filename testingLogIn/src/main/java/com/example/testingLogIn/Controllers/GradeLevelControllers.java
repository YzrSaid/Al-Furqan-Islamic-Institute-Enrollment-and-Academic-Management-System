package com.example.testingLogIn.Controllers;

import com.example.testingLogIn.Models.GradeLevel;
import com.example.testingLogIn.Services.GradeLevelServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/gradelevel")
@Controller
public class GradeLevelControllers {

    @Autowired
    GradeLevelServices gradeLevelServices;

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/all")
    public ResponseEntity<List<GradeLevel>> getAllGradeLevels(){
        return new ResponseEntity<>(gradeLevelServices.getAllGradeLevels(),HttpStatus.OK);
    }

    @PostMapping("/add/{levelName}")
    public ResponseEntity<String> addGradeLevel(@PathVariable String levelName){
        try{
            if(gradeLevelServices.addNewGradeLevel(levelName))
                return new ResponseEntity<>("New Grade Level Added Successfulyy",HttpStatus.OK);
            else
                return new ResponseEntity<>("Grade Level Name Already Exist", HttpStatus.BAD_REQUEST);
        }catch(Exception e){
            return new ResponseEntity<>("Process Failed",HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateGradeLevel(@RequestBody GradeLevel newGradeLevel){
        try{
            if(gradeLevelServices.updateGradeLevel(newGradeLevel))
                return new ResponseEntity<>("Grade Level Has Been Updated Successfulyy", HttpStatus.OK);
            else
                return new ResponseEntity<>("Grade Level Not Found",HttpStatus.NOT_FOUND);
        }catch(Exception e){
            return new ResponseEntity<>("Process Failed",HttpStatus.CONFLICT);
        }
    }
    
    @DeleteMapping("/{levelId}")
    public ResponseEntity<String> deleteGradeLevel(@PathVariable int levelId){
        try{
            if(gradeLevelServices.deleteGradeLevel(levelId))
                return new ResponseEntity<>("Grade Level Has Been Deleted Successfulyy", HttpStatus.OK);
            else
                return new ResponseEntity<>("Grade Level Not Found",HttpStatus.NOT_FOUND);
        }catch(Exception e){
            return new ResponseEntity<>("Process Failed",HttpStatus.CONFLICT);
        }
    }
}
