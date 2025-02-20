package com.example.testingLogIn.Services;

import com.example.testingLogIn.ModelDTO.SectionDTO;
import com.example.testingLogIn.ModelDTO.TeacherDTO;
import com.example.testingLogIn.Models.GradeLevel;
import com.example.testingLogIn.Models.Section;
import com.example.testingLogIn.Models.Teacher;
import com.example.testingLogIn.Repositories.GradeLevelRepo;
import com.example.testingLogIn.Repositories.SectionRepo;
import com.example.testingLogIn.Repositories.TeacherRepo;
import com.example.testingLogIn.WebsiteSecurityConfiguration.UserModel;
import com.example.testingLogIn.WebsiteSecurityConfiguration.UserRepo;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author magno
 */
@Service
public class SectionServices {
    
    private final GradeLevelRepo gradeRepo;
    private final SectionRepo sectionRepo;
    private final UserRepo userRepo;
    private final TeacherRepo teacherRepo;

    @Autowired
    public SectionServices(GradeLevelRepo gradeRepo, SectionRepo sectionRepo, UserRepo userRepo,TeacherRepo teacherRepo) {
        this.gradeRepo = gradeRepo;
        this.sectionRepo = sectionRepo;
        this.userRepo = userRepo;
        this.teacherRepo = teacherRepo;
    }
    //Get by Grade Level Name
    public List<SectionDTO> getSectionsByLevel(String gradeLevel){
        return sectionRepo.findAll().stream()
                          .filter(section -> section.getLevel().getLevelName().toLowerCase()
                                                    .equals(gradeLevel.toLowerCase())
                                            && section.isNotDeleted())
                            .map(Section::toSectionDTO)
                            .collect(Collectors.toList());
    }
    
    //FOR ADDING NEW SECTION
    public int addSection(SectionDTO sectionDTO){
        UserModel user= getUserByFullName(sectionDTO.getAdviserName());
        GradeLevel gradeLevel = getGradeLevel(sectionDTO.getGradeLevelName());
        int result = user == null ?            1:
                     gradeLevel == null ?      2:
                     doesTeacherHaveAdvisory
                                 (user) ? 3:
                     doesSectionNameExist
        (sectionDTO.getSectionName()
                .toLowerCase())              ? 4:
                                               5;
        
        switch(result){
            case 1:
                //Teacher Record/Account Does Not Exist
                return 1;
            case 2 :
                return 2;/*Grade Level Info Does Not Exist*/
            case 3:
                //If the Selected Teacher already have an Advisory Class
                return 3;
            case 4:
                //If the section name already exist
                return 4;
            default:
                Section newSection = Section.builder()
                                            .adviser(user)
                                            .level(gradeLevel)
                                            .sectionName(sectionDTO.getSectionName())
                                            .capacity(sectionDTO.getCapacity())
                                            .isNotDeleted(true)
                                            .build();
                sectionRepo.save(newSection);
                return 5;
        }
    }
    
    //GET ALL NOT DELETED SECTION
    public List<SectionDTO> getAllSections(){
        return sectionRepo.findAll().stream()
                          .filter(Section::isNotDeleted)
                          .map(Section::toSectionDTO)
                          .collect(Collectors.toList());
    }
    
    //GET THE SECTION
    public SectionDTO getSection(int sectionNumber){
        Section sec = sectionRepo.findById(sectionNumber).orElse(null);
        
        if(sec != null && sec.isNotDeleted())
            return sec.toSectionDTO();
        
        return null;                     
    }
    
    //UPDATE SECTION RECORD
    public boolean updateSection(SectionDTO sectionDTO){
        Section toUpdate = sectionRepo.findById(sectionDTO.getNumber()).orElse(null);
        
        if(toUpdate != null){
            toUpdate.setLevel(getGradeLevel(sectionDTO.getGradeLevelName()));
            toUpdate.setAdviser(getUserByFullName(sectionDTO.getAdviserName()));
            toUpdate.setSectionName(sectionDTO.getSectionName());
            toUpdate.setCapacity(sectionDTO.getCapacity());
            toUpdate.setNotDeleted(true);
            sectionRepo.save(toUpdate);
            
            return true;
        }
        return false;
    }
    
    //DELETING SECTION RECORD
    public boolean deleteSection(int sectionNumber){
        Section todelete = sectionRepo.findById(sectionNumber).orElse(null);
        
        if(todelete != null && todelete.isNotDeleted()){
            todelete.setNotDeleted(false);
            sectionRepo.save(todelete);
            return true;
        }
        return false;
    }
    
    //Return the List of Teachers that has no advisory class
    public List<TeacherDTO> getNoAdvisoryTeachers(){
        List<Integer> adviserStaffIds= new ArrayList<>();
        sectionRepo.findAll().stream()
                   .filter(Section::isNotDeleted)
                   .forEach(section -> adviserStaffIds.add(section.getAdviser().getStaffId()));
        
        return teacherRepo.findAll().stream()
                   .filter(teacher -> !adviserStaffIds.contains(teacher.getUser().getStaffId()))
                   .map(Teacher::mapper)
                   .collect(Collectors.toList());
    }
    
    private boolean doesSectionNameExist(String sectionName){
        return sectionRepo.findAll().stream()
                          .filter(section ->section.isNotDeleted() &&
                                            sectionName.equals(section.getSectionName().toLowerCase()))
                          .findFirst().orElse(null) != null;
    }
    
    private boolean doesTeacherHaveAdvisory(UserModel staff){
        return sectionRepo.findAll().stream()
                          .filter(advisory -> advisory.getAdviser().getStaffId() == staff.getStaffId() && 
                                              advisory.isNotDeleted())
                          .findFirst().orElse(null) !=null;
    }
    
    private UserModel getUserByFullName(String teacherName){
        return userRepo.findAll().stream()
                       .filter(user -> teacherName.contains(user.getFirstname()) &&
                                       teacherName.contains(user.getLastname()))
                       .findFirst().orElse(null);
    }
    
    private GradeLevel getGradeLevel(String levelName){
        return gradeRepo.findAll().stream()
                        .filter(grade -> levelName.equals(grade.getLevelName()) && 
                                         grade.isNotDeleted())
                        .findFirst().orElse(null);
    }
    
}
