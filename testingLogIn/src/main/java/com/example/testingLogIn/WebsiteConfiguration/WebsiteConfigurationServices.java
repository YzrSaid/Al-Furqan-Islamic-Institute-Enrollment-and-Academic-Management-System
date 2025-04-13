package com.example.testingLogIn.WebsiteConfiguration;

import com.example.testingLogIn.Enums.Semester;
import com.example.testingLogIn.Models.GradeLevel;
import com.example.testingLogIn.Repositories.GradeLevelRepo;
import com.example.testingLogIn.Services.sySemesterServices;
import com.example.testingLogIn.WebsiteSecurityConfiguration.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class WebsiteConfigurationServices {
    @Autowired
    private SchoolProfileRepo schoolProfileRepo;
    @Autowired
    private sySemesterServices semService;
    @Autowired
    private GradeLevelRepo gradeLevelRepo;

    public boolean updateSchoolInterface(WebsiteProfile profile) {
        try{
            byte[] newLogo = Optional.ofNullable(profile.getLogo()).map(logo -> {
                try {
                    return logo.getBytes();
                } catch (IOException e) {
                    return new byte[0];
                }
            }).orElse(new byte[0]);
            if(newLogo.length > 0){
                SchoolProfile schoolPhoto = schoolProfileRepo.findById("SchoolLogo").orElse(new SchoolProfile("SchoolLogo"));
                schoolPhoto.setKey_value(newLogo);
                schoolProfileRepo.save(schoolPhoto);
            }

            byte[] newCover = Optional.ofNullable(profile.getCover()).map(cover -> {
                try {
                    return cover.getBytes();
                } catch (IOException e) {
                    return new byte[0];
                }
            }).orElse(new byte[0]);
            if(newCover.length > 0){
                SchoolProfile schoolPhoto = schoolProfileRepo.findById("SchoolCover").orElse(new SchoolProfile("SchoolCover"));
                schoolPhoto.setKey_value(newCover);
                schoolProfileRepo.save(schoolPhoto);
            }

            if(profile.getGraduatingLevel() != null){
                byte [] gradeLevelByte;
                try(ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                    ObjectOutputStream objectOut = new ObjectOutputStream(byteStream)){
                    GradeLevel glvl = gradeLevelRepo.findById(profile.getGraduatingLevel()).orElse(null);
                    assert glvl != null;
                    objectOut.writeObject(glvl);
                    gradeLevelByte = byteStream.toByteArray();
                    SchoolProfile schoolGraduatingLevel = schoolProfileRepo.findById("GraduatingLevel").orElse(new SchoolProfile("GraduatingLevel"));
                    schoolGraduatingLevel.setKey_value(gradeLevelByte);
                    schoolProfileRepo.save(schoolGraduatingLevel);
                }catch (IOException ioe){
                    //do nothing
                }
            }

            SchoolProfile schoolName = schoolProfileRepo.findById("SchoolName").orElse(new SchoolProfile("SchoolName"));
            SchoolProfile schoolAddress = schoolProfileRepo.findById("SchoolAddress").orElse(new SchoolProfile("SchoolAddress"));
            SchoolProfile schoolEmail = schoolProfileRepo.findById("SchoolEmail").orElse(new SchoolProfile("SchoolEmail"));
            SchoolProfile schoolContactNum = schoolProfileRepo.findById("SchoolContactNum").orElse(new SchoolProfile("SchoolContactNum"));

            schoolName.setKey_value(profile.getSchoolName().getBytes(StandardCharsets.UTF_8));
            schoolAddress.setKey_value(profile.getSchoolAddress().getBytes(StandardCharsets.UTF_8));
            schoolEmail.setKey_value(profile.getSchoolEmail().getBytes(StandardCharsets.UTF_8));
            schoolContactNum.setKey_value(profile.getSchoolContact().getBytes(StandardCharsets.UTF_8));
            schoolProfileRepo.saveAll(List.of(schoolName,schoolAddress,schoolEmail,schoolContactNum));

            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Cacheable(value = "website-logo")
    public byte[] getLogo() {
        return schoolProfileRepo.findById("SchoolLogo").map(SchoolProfile::getKey_value).orElse(new byte[0]);
    }

    @Cacheable("website-cover")
    public byte[] getCover() {
        try {
            return schoolProfileRepo.findById("SchoolCover").map(SchoolProfile::getKey_value).orElse(new byte[0]);
        }catch (Exception e){
            return new byte[]{};
        }
    }

    @Cacheable("graduatingLevel")
    public Object getGraduatingLevel(){
        try{
            return new ResponseEntity<>(Objects.requireNonNull(schoolProfileRepo.findById("GraduatingLevel").map(glvl -> {
                GradeLevel graduatingLevel;
                try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(glvl.getKey_value()))) {
                    graduatingLevel = (GradeLevel) ois.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    return null;
                }
                return graduatingLevel;
            }).orElseThrow(NullPointerException::new)),HttpStatus.OK);
        }catch (NullPointerException npe){
            return  null;
        }
    }
    @Cacheable("schoolNum")
    public String getContact(){
        return schoolProfileRepo.findById("SchoolContactNum").map(name -> new String(name.getKey_value())).orElse("Contact Number Not Set");
    }

    @Cacheable("schoolName")
    public String getName(){
        return schoolProfileRepo.findById("SchoolName").map(name -> new String(name.getKey_value())).orElse("School Name Not Set");
    }

    @Cacheable("schoolEmail")
    public String getEmail(){
        return schoolProfileRepo.findById("SchoolEmail").map(name -> new String(name.getKey_value())).orElse("School Email Not Available");
    }

    @Cacheable("schoolAddress")
    public String getAddress(){
        return schoolProfileRepo.findById("SchoolAddress").map(name -> new String(name.getKey_value())).orElse("Contact Info Not Available");
    }

    @Cacheable("userRole")
    public String getRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserModel user) {
            return user.getRole().toString();
        }
        return "GUEST";
    }

    @Cacheable("userFullName")
    public String getFullName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserModel user) {
            return user.getFirstname() + " " + user.getLastname();
        }
        return "UNKNOWN";
    }

    @Cacheable("userFirstName")
    public String getFirstName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserModel user) {
            return user.getFirstname();
        }
        return "UNKNOWN";
    }

    @Cacheable("currentSY")
    public String getSchoolYear() {
        try {
            return semService.getCurrentActive().getSchoolYear().getSchoolYear();
        } catch (Exception e) {
            return "NOT FOUND";
        }
    }

    @Cacheable("currentSem")
    public String Sem() {
        try {
            return semService.getCurrentActive().getSem() == Semester.First ? "First" : "Second";
        } catch (Exception e) {
            return "NOT FOUND";
        }
    }
}
