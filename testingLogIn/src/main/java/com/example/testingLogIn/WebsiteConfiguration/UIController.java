package com.example.testingLogIn.WebsiteConfiguration;

import com.example.testingLogIn.Enums.Semester;
import com.example.testingLogIn.Models.GradeLevel;
import com.example.testingLogIn.Repositories.GradeLevelRepo;
import com.example.testingLogIn.Services.sySemesterServices;
import com.example.testingLogIn.WebsiteSecurityConfiguration.UserModel;
import jakarta.persistence.Cacheable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.RedirectView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequestMapping
@Controller
@ControllerAdvice
public class UIController {
    @Autowired
    private SchoolProfileRepo schoolProfileRepo;
    @Autowired
    private sySemesterServices semService;
    @Autowired
    private GradeLevelRepo gradeLevelRepo;

    @Autowired
    private WebsiteConfigurationServices configurationServices;

    @PostMapping("/website-config/update")
    public ResponseEntity<String> updateSchoolInterface(@ModelAttribute WebsiteProfile profile) {
        try{
            configurationServices.updateSchoolInterface(profile);
            return new ResponseEntity<>("School Profile edited successfully", HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Transaction failed. Unexpected Error", HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/website-logo")
    public ResponseEntity<byte[]> getSchoolLogo() {
        byte[] imageBytes = configurationServices.getLogo();
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(imageBytes);
    }

    @GetMapping("/website-cover")
    public ResponseEntity<byte[]> getSchoolCover() {
        byte[] imageBytes = configurationServices.getCover();
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(imageBytes);
    }

    @GetMapping("/graduatingLevel")
    public ResponseEntity<?> getGraduatingLevel(){
        try{
            return new ResponseEntity<>(configurationServices.getGraduatingLevel(),HttpStatus.OK);
        }catch (NullPointerException npe){
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @ModelAttribute("schoolNum")
    public String getSchoolContact(){
        return configurationServices.getContact();
    }

    @ModelAttribute("schoolName")
    public String getSchoolName(){
        return configurationServices.getName();
    }

    @ModelAttribute("schoolEmail")
    public String getSchoolEmail(){
        return configurationServices.getEmail();
    }

    @ModelAttribute("schoolAddress")
    public String getSchoolAddress(){
        return configurationServices.getAddress();
    }

    @ModelAttribute("userRole")
    public String getUserRole() {
        return configurationServices.getRole();
    }

    @ModelAttribute("userFullName")
    public String getUserFullName() {
        return configurationServices.getFullName();
    }

    @ModelAttribute("userFirstName")
    public String getUserFirstName() {
        return configurationServices.getFirstName();
    }

    @ModelAttribute("currentSY")
    public String currentSchoolYear() {
        return configurationServices.getSchoolYear();
    }

    @ModelAttribute("currentSem")
    public String currentSem() {
        return configurationServices.Sem();
    }
}
