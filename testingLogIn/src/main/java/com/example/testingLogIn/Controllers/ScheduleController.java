package com.example.testingLogIn.Controllers;

import com.example.testingLogIn.ModelDTO.ScheduleDTO;
import com.example.testingLogIn.Services.ScheduleServices;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    
        @GetMapping("/section/{sectionName}")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesBySection(@PathVariable String sectionName){
        try{
            return new ResponseEntity<>(scheduleService.getSchedulesBySection(sectionName),HttpStatus.OK);
        }catch(NullPointerException npe){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping()
    public ResponseEntity<String> getCurrentUsername(Authentication authentication) {
            return new ResponseEntity<>(authentication.getName(),HttpStatus.OK);
    }
    
    @PostMapping("/add")
    public ResponseEntity<Map<ScheduleDTO,String>> addSchedules(@RequestBody List<ScheduleDTO> schedules){
        try{
            Map<ScheduleDTO,String> schedsRejected = scheduleService.addNewSchedules(schedules);
            if(schedsRejected.isEmpty())
                return new ResponseEntity<>(HttpStatus.OK);
            else
                return new ResponseEntity<>(schedsRejected,HttpStatus.CONFLICT);
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
