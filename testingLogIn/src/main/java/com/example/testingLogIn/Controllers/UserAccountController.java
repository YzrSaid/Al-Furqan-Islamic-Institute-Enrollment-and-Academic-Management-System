package com.example.testingLogIn.Controllers;

import com.example.testingLogIn.Enums.RegistrationStatus;
import com.example.testingLogIn.ModelDTO.ScheduleDTO;
import com.example.testingLogIn.ModelDTO.StudentDTO;
import com.example.testingLogIn.ModelDTO.StudentGradesPerSem;
import com.example.testingLogIn.Models.AccountRegister;
import com.example.testingLogIn.CustomObjects.PagedResponse;
import com.example.testingLogIn.Models.Schedule;
import com.example.testingLogIn.Models.Section;
import com.example.testingLogIn.Services.ScheduleServices;
import com.example.testingLogIn.Services.StudentSubjectGradeServices;
import com.example.testingLogIn.WebsiteSecurityConfiguration.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.testingLogIn.ModelDTO.UserDTO;
import com.example.testingLogIn.Services.AccountRegisterServices;
import java.util.List;
import java.util.Optional;

@RequestMapping("/user")
@Controller
public class UserAccountController {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private AccountRegisterServices accountRegisterService;
    @Autowired
    private StudentSubjectGradeServices subjectGradeServices;
    @Autowired
    private ScheduleServices scheduleServices;

    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return new ResponseEntity<>(customUserDetailsService.getAllUsers(), HttpStatus.OK);}
    
    @GetMapping("/teachers")
    public ResponseEntity<List<UserDTO>> getAllTeachers() {
        return new ResponseEntity<>(customUserDetailsService.getTeachersAccount(), HttpStatus.OK);}
    
    @GetMapping("/current-logged-in")
    public ResponseEntity<UserDTO> getCurrentUserDTO(){
        return new ResponseEntity<>(customUserDetailsService.getCurrentlyLoggedInUserDTO(),HttpStatus.OK);
    }

    @GetMapping("/student-accounts")
    public ResponseEntity<PagedResponse> getStudentAccounts(@RequestParam(defaultValue = "1",required = false) int pageNo,
                                                            @RequestParam(defaultValue = "10",required = false) int pageSize,
                                                            @RequestParam(defaultValue = "") String q){
        try{
            return new ResponseEntity<>(customUserDetailsService.getStudentAccounts(pageNo,pageSize,q),HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/student/my-info")
    public ResponseEntity<StudentDTO> getLoggedStudentInfo(){
        return new ResponseEntity<>(customUserDetailsService.getCurrentlyLoggedInStudent(), HttpStatus.OK);
    }

    @GetMapping("/student/my-schedules")
    public ResponseEntity<List<ScheduleDTO>> getLoggedStudentGrades(){
        int sectionId = Optional.ofNullable(customUserDetailsService.getCurrentlyLoggedInUser().getStudent().getCurrentGradeSection()).map(Section::getNumber).orElse(0);
        return new ResponseEntity<>(scheduleServices.getSchedulesBySection(sectionId), HttpStatus.OK);
    }

    @GetMapping("/student/my-grades")
    public ResponseEntity<List<StudentGradesPerSem>> getLoggedStudentSchedules(){
        int studentId = customUserDetailsService.getCurrentlyLoggedInUser().getStudent().getStudentId();
        return new ResponseEntity<>(subjectGradeServices.getStudentGradesBySemester(studentId), HttpStatus.OK);
    }
    
    @PostMapping("/register")
    public ResponseEntity<String> registerAccount(@RequestBody AccountRegister toRegister){
        if(customUserDetailsService.usernameExist(toRegister.getUsername()))
            return new ResponseEntity<>("Email Already Taken",HttpStatus.CONFLICT);
        else{
            toRegister.setStatus(RegistrationStatus.PENDING);
            accountRegisterService.registerAccount(toRegister);
            return new ResponseEntity<>("Account confirmation has been sent successfully",HttpStatus.OK);
        }
    }
    @PutMapping("/restrict/{id}")
    public ResponseEntity<String> restrictUserAccount(@PathVariable int id){
        if(customUserDetailsService.restrictUser(id))
            return new ResponseEntity<>("Account Restricted Successfully",HttpStatus.OK);
        else
            return new ResponseEntity<>("Process Failed",HttpStatus.CONFLICT);
    }

    @PutMapping("/unrestrict/{id}")
    public ResponseEntity<String> unrestrictUserAccount(@PathVariable int id) {
        if (customUserDetailsService.unrestrictUser(id))
            return new ResponseEntity<>("Account Unrestricted Successfully", HttpStatus.OK);
        else
            return new ResponseEntity<>("Process Failed", HttpStatus.CONFLICT);
    }
}
