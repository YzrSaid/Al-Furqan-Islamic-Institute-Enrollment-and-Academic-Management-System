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


    @GetMapping("/section")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesBySection(@RequestParam int sectionId){
        return new ResponseEntity<>(scheduleService.getSchedulesBySection(sectionId),HttpStatus.OK);
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
    public ResponseEntity<Object> addSchedules(@RequestBody ScheduleDTO schedule){
        try{
            Map<Integer,ScheduleDTO> res = scheduleService.addNewSchedule(schedule);
            if (!res.isEmpty()) {
                Integer result = res.keySet().iterator().next(); // Safely get first key
                switch(result){
                    case 1:
                        return new ResponseEntity<>("Conflict with the Teacher's Existing Schedule",HttpStatus.CONFLICT);
                    case 2:
                        return new ResponseEntity<>("Conflict with the Section's Existing Schedule",HttpStatus.CONFLICT);
                    case 3:
                        return new ResponseEntity<>("Someone is already handling this subject on this section",HttpStatus.CONFLICT);
                    default:
                        return new ResponseEntity<>(res.get(result),HttpStatus.OK);
                }
            }else
                throw new Exception();
        }catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Server conflict.",HttpStatus.BAD_REQUEST);
        }
    }
    
    @PutMapping("/update")
    public ResponseEntity<Object> updateSchedule(@RequestBody ScheduleDTO schedDTO){
        try{
            Map<Integer,ScheduleDTO> res = scheduleService.updateSchedule(schedDTO);
            int result = res.keySet().iterator().next();
            switch(result){
                case 1:
                    return new ResponseEntity<>("Conflict with the Teacher's other existing schedule",HttpStatus.CONFLICT);
                case 2:
                    return new ResponseEntity<>("Conflict with the Section's other existing schedule",HttpStatus.CONFLICT);
                case 3:
                    return new ResponseEntity<>("Someone is already handling this subject on this section",HttpStatus.OK);
                case 4:
                    return new ResponseEntity<>(res.get(result),HttpStatus.OK);
                default:
                    return new ResponseEntity<>("Schedule Not Found",HttpStatus.NOT_FOUND);
            }
        }catch (NullPointerException npe){
            return new ResponseEntity<>("Schedule not found",HttpStatus.NOT_FOUND);
        }catch (UnknownError u){
            return new ResponseEntity<>("Selected teacher not found",HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>("Server error",HttpStatus.CONFLICT);
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
            return new ResponseEntity<>("You do not have this subject in your current teaching schedule. Please check your assigned subjects or contact the administrator for clarification.",HttpStatus.UNAUTHORIZED);
        }
    }
}
