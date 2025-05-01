package com.example.testingLogIn.Services;

import com.example.testingLogIn.CountersRepositories.SectionStudentCountServices;
import com.example.testingLogIn.CustomComparator.SectionComparator;
import com.example.testingLogIn.Enums.Role;
import com.example.testingLogIn.ModelDTO.SectionDTO;
import com.example.testingLogIn.ModelDTO.StudentDTO;
import com.example.testingLogIn.ModelDTO.UserDTO;
import com.example.testingLogIn.Models.GradeLevel;
import com.example.testingLogIn.Models.SchoolYearSemester;
import com.example.testingLogIn.Models.Section;
import com.example.testingLogIn.Repositories.*;
import com.example.testingLogIn.StatisticsModel.SectionStudentCount;
import com.example.testingLogIn.WebsiteSecurityConfiguration.UserModel;
import com.example.testingLogIn.WebsiteSecurityConfiguration.UserRepo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
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
    private final sySemesterRepo semRepo;
    private final EnrollmentServices enrollmentServices;
    private final SectionStudentCountServices sscService;
    private final EnrollmentRepo enrollmentRepo;

    @Autowired
    public SectionServices(GradeLevelRepo gradeRepo, SectionRepo sectionRepo, UserRepo userRepo, sySemesterRepo semRepo, EnrollmentServices enrollmentServices, SectionStudentCountServices sscService, EnrollmentRepo enrollmentRepo) {
        this.gradeRepo = gradeRepo;
        this.sectionRepo = sectionRepo;
        this.userRepo = userRepo;
        this.semRepo = semRepo;
        this.enrollmentServices = enrollmentServices;
        this.sscService = sscService;
        this.enrollmentRepo = enrollmentRepo;
    }

    //Get by Grade Level Name
    public List<SectionDTO> getSectionsByLevel(String gradeLevel){
        return sectionRepo.findAll().stream()
                          .filter(section -> section.getLevel().getLevelName()
                                                    .equalsIgnoreCase(gradeLevel)
                                            && section.isNotDeleted())
                            .map(Section::toSectionDTO)
                            .collect(Collectors.toList());
    }
    
    //FOR ADDING NEW SECTION
    public int addSection(SectionDTO sectionDTO){
        UserModel user= Optional.ofNullable(getTeacherById(sectionDTO.getAdviserId())).orElseThrow(()->new NullPointerException("Teacher Information Not Found"));
        GradeLevel gradeLevel = Optional.ofNullable(getGradeLevel(sectionDTO.getGradeLevelName())).orElseThrow(()->new NullPointerException("Grade Level Information Not Found"));
        if(getSectionByName(sectionDTO.getSectionName()) != null)
            throw new IllegalArgumentException("Section name already exists");
        int result = doesTeacherHaveAdvisory(user) ? 3: 5;

        if (result == 5) {
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
        return result;
    }

    public List<StudentDTO> getEnrolledStudentToSection(int sectionId){
        return enrollmentServices.getEnrolledStudentsBySection(sectionId);
    }

    public Map<String,InputStreamResource> downloadEnrolledStudentToSection(int sectionId){
        Section section = sectionRepo.findById(sectionId).orElseThrow(()->new NullPointerException("Section Information Not Found"));
        SchoolYearSemester sem = Optional.ofNullable(semRepo.findCurrentActive()).orElseThrow(()->new NullPointerException("School Year Not Found"));
        String fileName = section.getSectionName()+"_"+sem.toString()+"Records.csv";
        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        try {
            boas.write("Student Name\n".getBytes(StandardCharsets.UTF_8));
            for(StudentDTO student : enrollmentServices.getEnrolledStudentsBySection(sectionId))
                boas.write((student.getFullName()+"\n").getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Map.of(fileName,new InputStreamResource(new ByteArrayInputStream(boas.toByteArray())));
    }

    public List<SectionDTO> getAllSections(boolean willCountStud,String sortBy, String search){
        int activeSemId = Optional.ofNullable(semRepo.findCurrentActive()).map(SchoolYearSemester::getSySemNumber).orElse(0);
        SectionComparator sectionComparator = new SectionComparator();
        if(willCountStud)
            return sectionRepo.findSectionsAndGradeLevelNotDeleted(("%"+search.toLowerCase()+"%")).stream()
                    .map(Section::toSectionDTO)
                    .peek(sec -> sec.setStudentEnrolledCount(Optional.ofNullable
                            (sscService.findSectionCount(sec.getNumber(),activeSemId))
                            .map(SectionStudentCount::getStudentCount).orElse(0)))
                    .sorted((sec1,sec2) -> sectionComparator.myMethodFactory(sec1,sec2,sortBy))
                    .collect(Collectors.toList());

        //this is for grade management
        return sectionRepo.findSectionsAndGradeLevelNotDeleted(null).stream()
                          .map(Section::toSectionDTO)
                          .collect(Collectors.toList());
    }

    public Section getSectionById(int sectionNumber){
        return sectionRepo.findById(sectionNumber).orElse(null);
    }

    public SectionDTO getSection(int sectionNumber){
        return sectionRepo.findById(sectionNumber)
                .map(Section::toSectionDTO)
                .orElseThrow(()->new NullPointerException("Section Record Not Found"));
    }

    //UPDATE SECTION RECORD
    public void updateSection(SectionDTO sectionDTO){
        Section toUpdate = sectionRepo.findById(sectionDTO.getNumber()).orElseThrow(()->new NullPointerException("Section Record Not Found"));
        Section otherSec = getSectionByName(sectionDTO.getSectionName());
        if(otherSec != null && otherSec.getNumber() != toUpdate.getNumber())
            throw new IllegalArgumentException("Section name already exists");
        GradeLevel newGradeLevel = gradeRepo.findById(sectionDTO.getLevelId()).orElseThrow(()-> new NullPointerException("Grade Level Record Not Found"));

        SchoolYearSemester sem = semRepo.findCurrentActive();
        int semId = Optional.ofNullable(sem).map(SchoolYearSemester::getSySemNumber).orElse(0);
        int enrollmentCount = enrollmentRepo.countSectionGradeEnrollment(toUpdate.getNumber(),semId,null).orElse(0);
        if(newGradeLevel.getLevelId() != toUpdate.getLevel().getLevelId()){
            if(semId != 0){
                if(enrollmentCount > 0)
                    throw new IllegalArgumentException("Cannot change grade level due to ongoing enrollment related to this section");
            }
        }

        if(enrollmentCount > sectionDTO.getCapacity())
            throw new IllegalArgumentException("The new capacity conflicts with current student enrollments in this section.");

        toUpdate.setLevel(newGradeLevel);
        toUpdate.setAdviser(userRepo.findById(sectionDTO.getAdviserId()).orElseThrow(()->new NullPointerException("Teacher Record Not Found")));
        toUpdate.setSectionName(sectionDTO.getSectionName());
        toUpdate.setCapacity(sectionDTO.getCapacity());
        toUpdate.setNotDeleted(true);
        sectionRepo.save(toUpdate);
    }
    
    //DELETING SECTION RECORD
    public void deleteSection(int sectionNumber){
        Section todelete = sectionRepo.findById(sectionNumber).orElse(null);
        if(todelete == null || !todelete.isNotDeleted())
            throw new NullPointerException("Section Record not found");
        else if(enrollmentServices.getSectionTransactionCount(sectionNumber) > 0)
            throw new IllegalArgumentException("Deletion not allowed: This section is part of an ongoing enrollment transaction");

        todelete.setNotDeleted(false);
        sectionRepo.save(todelete);
    }
    
    //Return the List of Teachers that has no advisory class
    public List<UserDTO> getNoAdvisoryTeachers(){
        List<Integer> adviserStaffIds= new ArrayList<>();
        sectionRepo.findByIsNotDeletedTrue()
                   .forEach(section -> adviserStaffIds.add(section.getAdviser().getStaffId()));
        
        return userRepo.findAll().stream()
                   .filter(teacher -> teacher.getRole().equals(Role.TEACHER) && !adviserStaffIds.contains(teacher.getStaffId()))
                   .map(UserModel::mapperDTO)
                   .collect(Collectors.toList());
    }
    
    public SectionDTO getSectionByNameDTO(String sectionName){
         return Optional.ofNullable(getSectionByName(sectionName)).map(Section::toSectionDTO).orElseThrow(()-> new NullPointerException("Section Record Not Found"));
    }
    
    public Section getSectionByName(String sectionName){
        return sectionRepo.findAll().stream()
                          .filter(section -> section.isNotDeleted() &&
                                            section.getSectionName().equalsIgnoreCase(sectionName))
                          .findFirst().orElse(null);
    }
    
    private boolean doesTeacherHaveAdvisory(UserModel staff){
        return sectionRepo.findAll().stream()
                          .filter(advisory -> advisory.getAdviser().getStaffId() == staff.getStaffId() && 
                                              advisory.isNotDeleted())
                          .findFirst().orElse(null) !=null;
    }
    
    private UserModel getTeacherById(int staffId){
        return userRepo.findById(staffId).orElse(null);
    }
    
    private GradeLevel getGradeLevel(String levelName){
        return gradeRepo.findAll().stream()
                        .filter(grade -> levelName.equals(grade.getLevelName()) && 
                                         grade.isNotDeleted())
                        .findFirst().orElse(null);
    }
    
}
