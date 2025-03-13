package com.example.testingLogIn.Services;

import com.example.testingLogIn.ModelDTO.StudentSubjectGradeDTO;
import com.example.testingLogIn.Models.Enrollment;
import com.example.testingLogIn.Models.StudentSubjectGrade;
import com.example.testingLogIn.Repositories.StudentSubjectGradeRepo;
import com.example.testingLogIn.Repositories.SubjectRepo;
import com.example.testingLogIn.Repositories.sySemesterRepo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.aop.AopInvocationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
/**
 *
 * @author magno
 */
@Service
public class StudentSubjectGradeServices {
    private final StudentSubjectGradeRepo ssgRepo;
    private final SubjectRepo subjectRepo;
    private final sySemesterRepo semRepo;

    @Autowired
    public StudentSubjectGradeServices(StudentSubjectGradeRepo ssgRepo, SubjectRepo subjectRepo, sySemesterRepo semRepo) {
        this.ssgRepo = ssgRepo;
        this.subjectRepo = subjectRepo;
        this.semRepo = semRepo;
    }
    
    public boolean didStudentPassed(int studentId, int gradeLevelId){
        try{
            return ssgRepo.didStudentPassed(studentId, gradeLevelId);
        }catch(AopInvocationException aie){
            return false;
        }
    }

    @Async
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
    
    public List<StudentSubjectGradeDTO> getStudentsGradeBySectionSubject(int sectionId,int subjectId){
        return ssgRepo.getGradesBySectionSubjectSem(sectionId,
                                                    subjectId,
                                                    semRepo.findCurrentActive().getSySemNumber())
                        .stream().map(StudentSubjectGrade::DTOmapper)
                        .toList();
    }
    
    public Map<String,List<StudentSubjectGradeDTO>> getStudentGradesBySection(int sectionId){
        List<StudentSubjectGrade> gradesList = ssgRepo.getSectionGradesByCurrentSem(sectionId, semRepo.findCurrentActive().getSySemNumber());
        Map<String,List<StudentSubjectGradeDTO>> subjectStudGrades = new HashMap<>();
        
        gradesList
                .forEach(studGrade -> {
                    String subjectName = studGrade.getSubject().getSubjectName();
                    if(!subjectStudGrades.containsKey(subjectName))
                        subjectStudGrades.put(subjectName, new ArrayList<StudentSubjectGradeDTO>());
                    subjectStudGrades.get(subjectName).add(studGrade.DTOmapper());
                });
        
        return subjectStudGrades;
    }

    public Map<String,List<StudentSubjectGradeDTO>> getStudentGradesOfPreRequisite(int studentId,int preRequisiteId){
        List<StudentSubjectGrade> gradesList = ssgRepo.getGradesByStudentGradeLevel(studentId,preRequisiteId);
        Map<String,List<StudentSubjectGradeDTO>> subjectStudGrades = new HashMap<>();
        gradesList
                .forEach(studGrade -> {
                    String gradeLevelAndSectionSem = studGrade.getSection().getLevel().getLevelName()+" - "+
                            studGrade.getSection().getSectionName()+" : "+
                            studGrade.getSemester().getSchoolYear().getSchoolYear()+"-"+studGrade.getSemester().getSem()+ " sem";
                    if(!subjectStudGrades.containsKey(gradeLevelAndSectionSem))
                        subjectStudGrades.put(gradeLevelAndSectionSem, new ArrayList<StudentSubjectGradeDTO>());
                    subjectStudGrades.get(gradeLevelAndSectionSem).add(studGrade.DTOmapper());
                });

        return subjectStudGrades;
    }
    
    public Map<String,List<StudentSubjectGradeDTO>> getStudentGradesBySemester(int studentId){
        List<StudentSubjectGrade> gradesList = ssgRepo.getGradesByStudent(studentId);

        Map<String,List<StudentSubjectGradeDTO>> subjectStudGrades = new HashMap<>();
        
        gradesList
                .forEach(studGrade -> {
                    String gradeLevelAndSectionSem = studGrade.getSection().getLevel().getLevelName()+" - "+
                            studGrade.getSection().getSectionName()+" : "+
                            studGrade.getSemester().getSchoolYear().getSchoolYear()+"-"+studGrade.getSemester().getSem()+ " sem";
                    if(!subjectStudGrades.containsKey(gradeLevelAndSectionSem))
                        subjectStudGrades.put(gradeLevelAndSectionSem, new ArrayList<StudentSubjectGradeDTO>());
                    subjectStudGrades.get(gradeLevelAndSectionSem).add(studGrade.DTOmapper());
                });
        
        return subjectStudGrades;
    }
    
    public boolean updateStudentGrade(StudentSubjectGradeDTO studGrade){
        StudentSubjectGrade studSubGrade = ssgRepo.findById(studGrade.getStudGradeId()).orElse(null);
        if(studSubGrade == null)
            return false;
        
        studSubGrade.setSubjectGrade(studGrade.getSubjectGrade());
        ssgRepo.save(studSubGrade);
        return true;
    }
}
