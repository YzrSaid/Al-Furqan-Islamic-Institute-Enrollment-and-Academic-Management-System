package com.example.testingLogIn.Services;

import com.example.testingLogIn.AssociativeModels.StudentSubjectGrade;
import com.example.testingLogIn.Enums.Semester;
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

import com.example.testingLogIn.StatisticsModel.SubjectsStatistics;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

    @CacheEvict(value = {"subjectRates"}, allEntries = true)
    public boolean addNewSubject(int levelId,String subjectName, boolean applyNow){
        gradeLevelService.getGradeLevel(levelId);//icheck if nag exists ba ang grade level
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

    @CacheEvict(value = {"subjectRates"}, allEntries = true)
    public boolean updateSubjectDescription(SubjectDTO subject){
        Subject updatedSub = subjectRepo.findById(subject.getSubjectNumber()).orElseThrow(()->new NullPointerException("Subject Record Not Found"));
        List<Subject> existing = subjectRepo.findByNameNotEqualId(subject.getSubjectName().toLowerCase(), subject.getSubjectNumber());
        if(existing.isEmpty()){
            boolean isNotActiveBefore = !updatedSub.isCurrentlyActive();
            updatedSub.setGradeLevel(gradeLevelService.getByName(subject.getGradeLevel()));
            updatedSub.setSubjectName(subject.getSubjectName());
            updatedSub.setCurrentlyActive(subject.isWillApplyNow());
            subjectRepo.save(updatedSub);

            SchoolYearSemester sem = semServices.getCurrentActive();
            if(subject.isWillApplyNow()){
                if(sem != null){
                    int semId = sem.getSySemNumber();
                    CompletableFuture.runAsync(()->{
                        List<StudentSubjectGrade> studentGrades = new ArrayList<>();
                        ssgRepo.findEnrolledStudentsNoRecord(subject.getSubjectNumber(),semId)
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
            if(sem != null)
                CompletableFuture.runAsync(()->ssgRepo.updateSubjectGradeStatus(subject.getSubjectNumber(),sem.getSySemNumber(),subject.isWillApplyNow()));
            return true;
        }
        return false;
    }

    @CacheEvict(value = {"subjectRates"}, allEntries = true)
    public void deleteSubject(int subjectNumber){
        Subject todelete = subjectRepo.findById(subjectNumber).orElseThrow(NullPointerException::new);
        todelete.setNotDeleted(false);
        subjectRepo.save(todelete);
        if(todelete.isCurrentlyActive()){
            SchoolYearSemester sem = semServices.getCurrentActive();
            if(sem != null){
                int semId = sem.getSySemNumber();
                CompletableFuture.runAsync(()->ssgRepo.updateSubjectGradeStatus(todelete.getSubjectNumber(),semId,false));
            }
        }
    }

    @Cacheable(value = "subjectRates", key = "#levelId + #sy + #semKey")
    public SubjectsStatistics getSubjectStatistics(Integer levelId, Integer sy, Semester sem, String semKey){
        Integer schoolYear = sy == 0 ? null : sy;
        SubjectsStatistics subjectStats = new SubjectsStatistics(new ArrayList<String>(),new ArrayList<Float>());

        if(levelId == null || levelId == 0)
            return subjectStats;

        ssgRepo.findSemesterUniqueSubjects(schoolYear,sem,levelId)
                .forEach(subj -> {
                    subjectStats.getAvgGrades().add((float) NonModelServices.adjustDecimal(
                                    ssgRepo.getSubjectAverageGrade(subj.getSubjectNumber(),schoolYear,sem)
                                            .orElse(0.0F)));
                    subjectStats.getSubjectNames().add(subj.getSubjectName());
                });

        return subjectStats;
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
