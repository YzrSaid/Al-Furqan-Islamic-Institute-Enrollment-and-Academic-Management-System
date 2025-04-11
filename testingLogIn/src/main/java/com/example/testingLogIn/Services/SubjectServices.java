package com.example.testingLogIn.Services;

import com.example.testingLogIn.AssociativeModels.StudentSubjectGrade;
import com.example.testingLogIn.ModelDTO.SectionDTO;
import com.example.testingLogIn.ModelDTO.SubjectDTO;
import com.example.testingLogIn.Models.SchoolYearSemester;
import com.example.testingLogIn.Models.Subject;
import com.example.testingLogIn.Repositories.EnrollmentRepo;
import com.example.testingLogIn.Repositories.StudentSubjectGradeRepo;
import com.example.testingLogIn.Repositories.SubjectRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

/**
 *
 * @author magno
 */
@Service
public class SubjectServices {

    private final SubjectRepo subjectRepo;
    private final GradeLevelServices gradeLevelService;
    private final SectionServices sectionService;
    private final EnrollmentRepo enrollmentRepo;
    private final StudentSubjectGradeRepo ssgRepo;
    private final sySemesterServices semServices;

    public SubjectServices(SubjectRepo subjectRepo, GradeLevelServices gradeLevelService, SectionServices sectionService, EnrollmentRepo enrollmentRepo, StudentSubjectGradeRepo ssgRepo, sySemesterServices semServices) {
        this.subjectRepo = subjectRepo;
        this.gradeLevelService = gradeLevelService;
        this.sectionService = sectionService;
        this.enrollmentRepo = enrollmentRepo;
        this.ssgRepo = ssgRepo;
        this.semServices = semServices;
    }

    public List<SubjectDTO> getSubjectByGrade(String gradeLevel){
        return subjectRepo.findAll().stream()
                          .filter(subject ->subject.getGradeLevel().isNotDeleted() && 
                                            subject.getGradeLevel().getLevelName()
                                                   .equalsIgnoreCase(gradeLevel) &&
                                             subject.isNotDeleted())
                          .map(Subject::mapper)
                          .collect(Collectors.toList());
    }
    
    public List<SubjectDTO> getSectionSubjects(int sectionId){
        SectionDTO sec = sectionService.getSection(sectionId);
        
        return getSubjectByGrade(sec.getGradeLevelName());
    }
    
    public SubjectDTO getSubject(int subjectNumber){
        Subject sub = subjectRepo.findAll().stream()
                                 .filter(subj -> subj.getGradeLevel().isNotDeleted() &&
                                                subj.getSubjectNumber()==subjectNumber &&
                                                subj.isNotDeleted())
                                 .findFirst().orElse(null);
        
        if(sub != null)
            return SubjectToSubjectDTO(sub);
        else
            return null;      
    }
    
    public List<SubjectDTO> getAllSubjects(){
        return subjectRepo.findAll().stream()
                          .filter(subj -> subj.isNotDeleted() &&
                                            subj.getGradeLevel().isNotDeleted())
                          .map(this::SubjectToSubjectDTO)
                          .collect(Collectors.toList());
    }
            
    public boolean addNewSubject(int levelId,String subjectName, boolean applyNow){
        Optional.ofNullable(gradeLevelService.getGradeLevel(levelId)).orElseThrow(NullPointerException::new);
        
        if(!doesSubjectNameExist(levelId,subjectName)){
            Subject sub=new Subject();
            sub.setNotDeleted(true);
            sub.setGradeLevel(gradeLevelService.getGradeLevel(levelId));
            sub.setSubjectName(subjectName);
            sub.setCurrentlyActive(applyNow);
            subjectRepo.save(sub);

            if(sub.isCurrentlyActive()){
                SchoolYearSemester sem = semServices.getCurrentActive();
                if(sem != null){
                    int semId = sem.getSySemNumber();
                    CompletableFuture.runAsync(()->{
                        List<StudentSubjectGrade> studentGrades = new ArrayList<>();
                        enrollmentRepo.getCurrentlyEnrolledToGrade(levelId,semId)
                                .forEach(student ->{
                                    studentGrades.add(StudentSubjectGrade.builder()
                                                    .student(student)
                                                    .section(student.getCurrentGradeSection())
                                                    .subject(sub)
                                                    .isNotDeleted(true)
                                                    .subjectGrade(null)
                                                    .isDropped(false)
                                                    .semester(sem)
                                            .build());
                                });
                        ssgRepo.saveAll(studentGrades);
                    });
                }
            }
            
            return true;
        }else
            return false;
    }
    
    public Subject getByName(String subjectName){
        return subjectRepo.findAll().stream()
                          .filter(subject -> subject.isNotDeleted() &&
                                             subjectName.equalsIgnoreCase(subject.getSubjectName()))
                          //.map(Subject::mapper)
                            .findFirst().orElse(null);
    }
    
    public boolean updateSubjectDescription(SubjectDTO subject){
        Subject updatedSub = subjectRepo.findById(subject.getSubjectNumber()).orElseThrow(NullPointerException::new);
        List<Subject> existing = subjectRepo.findByNameNotEqualId(subject.getSubjectName().toLowerCase(), subject.getSubjectNumber());
        if(existing.isEmpty()){
            boolean isNotActiveBefore = !updatedSub.isCurrentlyActive();
            updatedSub.setGradeLevel(gradeLevelService.getByName(subject.getGradeLevel()));
            updatedSub.setSubjectName(subject.getSubjectName());
            updatedSub.setCurrentlyActive(subject.isWillApplyNow());
            subjectRepo.save(updatedSub);

            if(isNotActiveBefore && subject.isWillApplyNow()){
                SchoolYearSemester sem = semServices.getCurrentActive();
                if(sem != null){
                    int semId = sem.getSySemNumber();
                    CompletableFuture.runAsync(()->{
                        List<StudentSubjectGrade> studentGrades = new ArrayList<>();
                        enrollmentRepo.getCurrentlyEnrolledToGrade(updatedSub.getGradeLevel().getLevelId(),semId)
                                .forEach(student ->{
                                    studentGrades.add(StudentSubjectGrade.builder()
                                            .student(student)
                                            .section(student.getCurrentGradeSection())
                                            .subject(updatedSub)
                                            .isNotDeleted(true)
                                            .subjectGrade(null)
                                            .isDropped(false)
                                            .semester(sem)
                                            .build());
                                });
                        ssgRepo.saveAll(studentGrades);
                    });
                }
            }
            return true;
        }
        return false;
    }
    
    public void deleteSubject(int subjectNumber){
        Subject todelete = subjectRepo.findById(subjectNumber).orElseThrow(NullPointerException::new);
        todelete.setNotDeleted(false);
        subjectRepo.save(todelete);
        if(todelete.isCurrentlyActive()){
            SchoolYearSemester sem = semServices.getCurrentActive();
            if(sem != null){
                int semId = sem.getSySemNumber();
                CompletableFuture.runAsync(()->ssgRepo.deleteStudentSubjectGrade(todelete.getSubjectNumber(),semId));
            }
        }
    }
     
    private SubjectDTO SubjectToSubjectDTO(Subject subject){
         return SubjectDTO.builder()
                          .subjectNumber(subject.getSubjectNumber())
                          .gradeLevel(subject.getGradeLevel().getLevelName())
                          .subjectName(subject.getSubjectName())
                          .isNotDeleted(subject.isNotDeleted())
                          .build();
     }
    
    private boolean doesSubjectNameExist(int levelId,String subjectName){
        return subjectRepo.findAll().stream()
                          .filter(sub ->sub.isNotDeleted() && 
                                        subjectName.equals(sub.getSubjectName()) &&
                                        sub.getGradeLevel().getLevelId() == levelId)
                          .findFirst().orElse(null) != null;
    }
}
