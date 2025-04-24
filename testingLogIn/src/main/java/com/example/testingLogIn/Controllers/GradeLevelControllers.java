package com.example.testingLogIn.Controllers;

import com.example.testingLogIn.ModelDTO.GradeLevelDTO;
import com.example.testingLogIn.Models.GradeLevel;
import com.example.testingLogIn.Services.GradeLevelServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/gradelevel")
@Controller
public class GradeLevelControllers {

    @Autowired
    GradeLevelServices gradeLevelServices;

//    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/all")
    public ResponseEntity<List<GradeLevelDTO>> getAllGradeLevels() {
        try{
            return new ResponseEntity<>(gradeLevelServices.getAllGradeLevelsDTO(), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getByName(@RequestParam String name){
        try{
            return new ResponseEntity<>(gradeLevelServices.getByName(name),HttpStatus.OK);
        }catch (NullPointerException npe){
            return new ResponseEntity<>("Grade Level Not Found",HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>("Server Error. Contact SlimFordy",HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/no-pre-requisite")
    public ResponseEntity<List<GradeLevel>> getStartingGrades(){
        try{
            return new ResponseEntity<>(gradeLevelServices.getGradeLevelNoPreRequisite(),HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/successor/{levelId}")
    public ResponseEntity<List<GradeLevel>> getGradeLevelSuccessor(@PathVariable int levelId){
        try{
            return new ResponseEntity<>(gradeLevelServices.getGradeLevelSuccessor(levelId),HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
    @GetMapping("/no-successor-grade-level")
    public ResponseEntity<List<GradeLevel>> getNoSuccessorGradeLevel(){
        try{
            return new ResponseEntity<>(gradeLevelServices.getNoSuccessorsGradeLevels(),HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<String> addGradeLevel(@RequestParam String levelName,
                                                @RequestParam String preRequisite) {
        gradeLevelServices.addNewGradeLevel(levelName, preRequisite);
        return new ResponseEntity<>("New Grade Level Added Successfully", HttpStatus.OK);
    }

    @GetMapping("/{levelId}")
    public ResponseEntity<GradeLevelDTO> getGradeLevelById(@PathVariable int levelId) {
        return new ResponseEntity<>(gradeLevelServices.getGradeLevelDTO(levelId), HttpStatus.OK);
    }

    @PutMapping("/update-grade-level")
    public ResponseEntity<String> updateGradeLevel(@RequestBody GradeLevelDTO newGradeLevel) {
        gradeLevelServices.updateGradeLevel(newGradeLevel);
        return new ResponseEntity<>("Grade Level Has Been Updated Successfully", HttpStatus.OK);
    }

    @DeleteMapping("/{levelId}")
    public ResponseEntity<String> deleteGradeLevel(@PathVariable int levelId) {
        gradeLevelServices.deleteGradeLevel(levelId);
        return new ResponseEntity<>("Grade Level Has Been Deleted Successfully", HttpStatus.OK);
    }

    @GetMapping("/for-transferee/{levelId}")
    public ResponseEntity<List<GradeLevelDTO>> testMe(@PathVariable int levelId){
        try {
            return new ResponseEntity<>(gradeLevelServices.preRequisiteOfPreRequisite(levelId), HttpStatus.OK);
        }catch (NullPointerException npe){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
