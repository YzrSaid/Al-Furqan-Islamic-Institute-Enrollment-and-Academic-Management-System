package com.example.testingLogIn.StatisticsModel;

import com.example.testingLogIn.Enums.Semester;
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

    @GetMapping("/counts")
    public ResponseEntity<CounterObject> getCounts(@RequestParam(required = false) Integer schoolYear,
                                                   @RequestParam(required = false) String semester){
        schoolYear = schoolYear == 0 ? null : schoolYear;
        Semester sem =  semester.equalsIgnoreCase("first") ? Semester.First :
                        semester.equalsIgnoreCase("second") ? Semester.Second :
                        null;
        try{
            return new ResponseEntity<>(statisticsServices.getCounts(schoolYear,sem), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/rates")
    public ResponseEntity<GradeLevelRates> getRates(@RequestParam(required = false,defaultValue = "0") Integer schoolYear,
                                                   @RequestParam(required = false,defaultValue = "All") String semester,
                                                    @RequestParam boolean didPassed){
        schoolYear = schoolYear == 0 ? null : schoolYear;
        Semester sem =  semester.equalsIgnoreCase("first") ? Semester.First :
                semester.equalsIgnoreCase("second") ? Semester.Second :
                        null;
        try{
            return new ResponseEntity<>(statisticsServices.getGradeLevelRates(schoolYear,sem,didPassed), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
}
