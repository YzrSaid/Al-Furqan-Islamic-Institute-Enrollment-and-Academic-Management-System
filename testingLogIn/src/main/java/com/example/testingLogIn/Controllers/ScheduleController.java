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
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @GetMapping("/teacher/download-schedule/{teacherName}")
    public ResponseEntity<InputStreamResource> downloadTeacherSchedules(@PathVariable String teacherName){
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\""+teacherName+"_Schedules.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(scheduleService.downloadTeacherSchedule(teacherName));
    }

    @GetMapping("/my-schedules")
    public ResponseEntity<List<ScheduleDTO>> getTeacherSchedule(){
        try{
            return new ResponseEntity<>(scheduleService.getLoggedInTeacherSched(),HttpStatus.OK);
        }catch(NullPointerException npe){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/download/section")
    public ResponseEntity<InputStreamResource> getSectionSchedules(@RequestParam int sectionId){
        List<?> toreturn = scheduleService.downloadSectionSchedules(sectionId);

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\""+toreturn.getFirst().toString()+"_Schedules.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body((InputStreamResource) toreturn.getLast());
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
    public ResponseEntity<?> addSchedules(@RequestBody ScheduleDTO schedule){
        System.out.println("Added");
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
            return new ResponseEntity<>("Server conflict.",HttpStatus.BAD_REQUEST);
        }
    }
    
    @PutMapping("/update")
    public ResponseEntity<ScheduleDTO> updateSchedule(@RequestBody ScheduleDTO schedDTO){
        ScheduleDTO schedule = scheduleService.updateSchedule(schedDTO);
        return new ResponseEntity<>(schedule,HttpStatus.OK);
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
