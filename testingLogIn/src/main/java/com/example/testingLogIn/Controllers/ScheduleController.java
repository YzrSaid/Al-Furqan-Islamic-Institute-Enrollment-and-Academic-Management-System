package com.example.testingLogIn.Controllers;

import com.example.testingLogIn.ModelDTO.ScheduleDTO;
import com.example.testingLogIn.Services.ScheduleServices;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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
    
    @GetMapping("/section/{sectionId}")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesBySection(@PathVariable int sectionId){
        try{
            return new ResponseEntity<>(scheduleService.getSchedulesBySection(sectionId),HttpStatus.OK);
        }catch(NullPointerException npe){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping("/section-subjects/{sectionId}")
    public ResponseEntity<Map<Integer,ScheduleDTO>> getTeachersBySectionScheds(@PathVariable int sectionId){
        try{
            return new ResponseEntity<>(scheduleService.getSubjectsUniqeTeacher(sectionId),HttpStatus.OK);
        }catch(NullPointerException npe){
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
}
