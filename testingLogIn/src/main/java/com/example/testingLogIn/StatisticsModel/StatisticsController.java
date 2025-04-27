package com.example.testingLogIn.StatisticsModel;

import com.example.testingLogIn.CustomObjects.PagedResponse;
import com.example.testingLogIn.Enums.Semester;
import com.example.testingLogIn.Models.GradeLevel;
import com.example.testingLogIn.Repositories.EnrollmentRepo;
import com.example.testingLogIn.Services.SubjectServices;
import jakarta.persistence.Cacheable;
import jakarta.persistence.GeneratedValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            return new ResponseEntity<>(statisticsServices.getCounts(schoolYear,sem), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/gender-rates")
    public ResponseEntity<GradeLevelRates> getGenderRates(@RequestParam(required = false) Integer schoolYear,
                                                          @RequestParam(required = false) String semester){
        Semester sem =  semester.equalsIgnoreCase("first") ? Semester.First :
                semester.equalsIgnoreCase("second") ? Semester.Second :
                        null;
        schoolYear = schoolYear==0? null :schoolYear;
        try{
            return new ResponseEntity<>(statisticsServices.getGenderCounts(schoolYear,sem), HttpStatus.OK);
        }catch (Exception e){
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

    @GetMapping("/enrollment-statistics")
    public ResponseEntity<PagedResponse> getEnrollmentRecordsByStatusPage(@RequestParam String status,
                                                                          @RequestParam(required = false,defaultValue = "1") int pageNo,
                                                                          @RequestParam(required = false,defaultValue = "10") int pageSize,
                                                                          @RequestParam(required = false) String search,
                                                                          @RequestParam(required = false) Integer sy,
                                                                          @RequestParam(required = false) String sem){
        try{
            return new ResponseEntity<>(statisticsServices.getEnrollmentStatistics(search, status, sy, sem, pageNo, pageSize),HttpStatus.OK);
        }catch (NullPointerException npe){
            npe.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/graduate-statistics")
    public ResponseEntity<PagedResponse> getGraduateStudents(@RequestParam(required = false,defaultValue = "1") int pageNo,
                                                            @RequestParam(required = false,defaultValue = "10") int pageSize,
                                                            @RequestParam(required = false) String search,
                                                            @RequestParam(required = false) Integer sy,
                                                            @RequestParam(required = false) String sem){
        try{
            return new ResponseEntity<>(statisticsServices.getGraduateStudents(search, sy, sem, pageNo, pageSize),HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/passing-statistics")
    public ResponseEntity<PagedResponse> getPassingRecords(@RequestParam(required = false,defaultValue = "1") int pageNo,
                                                           @RequestParam(required = false,defaultValue = "10") int pageSize,
                                                           @RequestParam(required = false) String search,
                                                           @RequestParam(required = false) Integer sy,
                                                           @RequestParam(required = false) String sem,
                                                           @RequestParam Integer levelId,
                                                           @RequestParam(required = false) Boolean didPassed){
        try{
            return new ResponseEntity<>(statisticsServices.getStudentsPassingRecords(search, sy, sem, pageNo, pageSize,didPassed,levelId),HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
}
