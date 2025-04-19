package com.example.testingLogIn.Controllers;

import com.example.testingLogIn.CustomObjects.EvaluationStatus;
import com.example.testingLogIn.CustomObjects.SubjectSectionCount;
import com.example.testingLogIn.ModelDTO.ScheduleDTO;
import com.example.testingLogIn.Services.ScheduleServices;

import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author magno
 */
@RequestMapping("/schedules")
@Controller
public class ScheduleController {
    
    @Autowired
    private ScheduleServices scheduleService;
    
    @GetMapping("/teacher/{teacherName}")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesByTeacher(@PathVariable String teacherName){
        try{
            return new ResponseEntity<>(scheduleService.getSchedulesByTeacher(teacherName),HttpStatus.OK);
        }catch(NullPointerException npe){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/my-schedules")
    public ResponseEntity<List<ScheduleDTO>> getTeacherSchedule(){
        try{
            return new ResponseEntity<>(scheduleService.getLoggedInTeacherSched(),HttpStatus.OK);
        }catch(NullPointerException npe){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping("/section/{sectionId}")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesBySection(@PathVariable int sectionId){
        try{
            return new ResponseEntity<>(scheduleService.getSchedulesBySection(sectionId),HttpStatus.OK);
        }catch(NullPointerException npe){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/section-subjects")
    public ResponseEntity<List<EvaluationStatus>> getTest(@RequestParam(required = false) Integer sectionId){
        try{
            return new ResponseEntity<>(scheduleService.getSectionSubjects(sectionId),HttpStatus.OK);
        }catch(NullPointerException npe){
            npe.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @PostMapping("/add")
    public ResponseEntity<String> addSchedules(@RequestBody ScheduleDTO schedule){
        try{
            int result = scheduleService.addNewSchedule(schedule);
            switch(result){
                case 1:
                    return new ResponseEntity<>("Conflict with the Teacher's Existing Schedule",HttpStatus.CONFLICT);
                case 2:
                    return new ResponseEntity<>("Conflict with the Section's Existing Schedule",HttpStatus.CONFLICT);
                case 3:
                    return new ResponseEntity<>("Someone is already handling this subject on this section",HttpStatus.CONFLICT);
                default:
                    return new ResponseEntity<>("Schedule Successfully Added",HttpStatus.OK);
            }
        }catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @PutMapping("/update")
    public ResponseEntity<String> updateSchedule(@RequestBody ScheduleDTO schedDTO){
        int result = scheduleService.updateSchedule(schedDTO);
        
        switch(result){
            case 1:
                return new ResponseEntity<>("Conflict with the Teacher's other existing schedule",HttpStatus.CONFLICT);
            case 2:
                return new ResponseEntity<>("Conflict with the Section's other existing schedule",HttpStatus.CONFLICT);
            case 3:
                return new ResponseEntity<>("Someone is already handling this subject on this section",HttpStatus.OK);
            case 4:
                return new ResponseEntity<>("Schedule Updated Successfully",HttpStatus.OK);
            default:
                return new ResponseEntity<>("Schedule Not Found",HttpStatus.NOT_FOUND);
        }
    }
    
    @DeleteMapping("/delete/{scheduleId}")
    public ResponseEntity<String> deleteSchedule(@PathVariable int scheduleId){
        if(scheduleService.deleteSchedule(scheduleId))
            return new ResponseEntity<>("Schedule Deleted Successfully",HttpStatus.OK);
        else
            return new ResponseEntity<>("Schedule Not Found",HttpStatus.NOT_FOUND);
    }

    //get the subjects being taught by the teacher
    @GetMapping("/test")
    public ResponseEntity<List<SubjectSectionCount>> testNow(){
        try {
            return new ResponseEntity<>(scheduleService.getTeacherSubjects(), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
    //kuhaon ang mga sections nga naay schedule ang kini nga maestra for the specific subject
    @GetMapping("/test/{subjectId}")
    public ResponseEntity<?> testNow(@PathVariable int subjectId){
        try {
            return new ResponseEntity<>(scheduleService.getSectionsBySubject(subjectId), HttpStatus.OK);
        }catch(NullPointerException e){
            return new ResponseEntity<>("Mini wala mn kay subject in ani nga ginatudlo. Hala hacker ka!!!",HttpStatus.UNAUTHORIZED);
        }
    }
}
