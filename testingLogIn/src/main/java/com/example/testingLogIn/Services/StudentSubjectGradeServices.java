package com.example.testingLogIn.Services;

import com.example.testingLogIn.Models.Enrollment;
import com.example.testingLogIn.Models.StudentSubjectGrade;
import com.example.testingLogIn.Repositories.StudentSubjectGradeRepo;
import com.example.testingLogIn.Repositories.SubjectRepo;
import org.springframework.aop.AopInvocationException;
import org.springframework.stereotype.Service;
/**
 *
 * @author magno
 */
@Service
public class StudentSubjectGradeServices {
    private final StudentSubjectGradeRepo ssgRepo;
    private final SubjectRepo subjectRepo;

    public StudentSubjectGradeServices(StudentSubjectGradeRepo ssgRepo, SubjectRepo subjectRepo) {
        this.ssgRepo = ssgRepo;
        this.subjectRepo = subjectRepo;
    }
    
    public boolean didStudentPassed(int studentId, int gradeLevelId){
        try{
            boolean result = ssgRepo.didStudentPassed(studentId, gradeLevelId);
            return result;
        }catch(AopInvocationException aie){
            System.out.println("Nag error");
            return false;
        }
    }
    
    public void addStudentGrades(Enrollment enrollment){
        subjectRepo.findAll().stream()
                    .filter(subject ->  subject.isNotDeleted() && 
                                        subject.getGradeLevel().getLevelId() == enrollment.getGradeLevelToEnroll().getLevelId())
                    .forEach(subject ->{
                        StudentSubjectGrade studSubGrade = StudentSubjectGrade.builder()
                                                            .student(enrollment.getStudent())
                                                            .subject(subject)
                                                            .section(enrollment.getSectionToEnroll())
                                                            .semester(enrollment.getSYSemester())
                                                            .subjectGrade(null)
                                                            .build();
                        ssgRepo.save(studSubGrade);
                    });
    }
}
