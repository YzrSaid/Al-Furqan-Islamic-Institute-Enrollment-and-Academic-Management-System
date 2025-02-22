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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
