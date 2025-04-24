package com.example.testingLogIn.StatisticsModel;

import com.example.testingLogIn.Enums.Semester;
import com.example.testingLogIn.Services.SubjectServices;
import jakarta.persistence.Cacheable;
import jakarta.persistence.GeneratedValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsServices statisticsServices;
    @Autowired
    private SubjectServices subjectServices;

    @GetMapping("/counts")
    public ResponseEntity<CounterObject> getCounts(@RequestParam(required = false) Integer schoolYear,
                                                   @RequestParam(required = false) String semester){
        Semester sem =  semester.equalsIgnoreCase("first") ? Semester.First :
                        semester.equalsIgnoreCase("second") ? Semester.Second :
                        null;
        try{
            return new ResponseEntity<>(statisticsServices.getCounts(schoolYear,sem,semester), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/rates")
    public ResponseEntity<GradeLevelRates> getRates(@RequestParam(required = false,defaultValue = "0") Integer schoolYear,
                                                   @RequestParam(required = false,defaultValue = "All") String semester,
                                                    @RequestParam boolean didPassed){
        String passed = didPassed ? "passed" : "failed";
        Semester sem =  semester.equalsIgnoreCase("first") ? Semester.First :
                semester.equalsIgnoreCase("second") ? Semester.Second :
                        null;
        try{
            return new ResponseEntity<>(statisticsServices.getGradeLevelRates(schoolYear,sem,didPassed, semester, passed), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/subject-rates")
    public ResponseEntity<SubjectsStatistics> getSubjectRates(@RequestParam(required = false,defaultValue = "0") Integer schoolYear,
                                                           @RequestParam(required = false,defaultValue = "All") String semester,
                                                           @RequestParam(required = false) Integer levelId){
        Semester sem =  semester.equalsIgnoreCase("first") ? Semester.First :
                semester.equalsIgnoreCase("second") ? Semester.Second :
                        null;
        try{
            return new ResponseEntity<>(subjectServices.getSubjectStatistics(levelId, schoolYear, sem, semester), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
}
