package com.example.testingLogIn.Services;

import com.example.testingLogIn.AssociativeModels.StudentSubjectGradeLogs;
import com.example.testingLogIn.Enums.Role;
import com.example.testingLogIn.ModelDTO.GradeLogsDTO;
import com.example.testingLogIn.ModelDTO.StudentGradesPerSem;
import com.example.testingLogIn.ModelDTO.StudentSubjectGradeDTO;
import com.example.testingLogIn.Models.Enrollment;
import com.example.testingLogIn.AssociativeModels.StudentSubjectGrade;
import com.example.testingLogIn.Models.SchoolYear;
import com.example.testingLogIn.Models.SchoolYearSemester;
import com.example.testingLogIn.Repositories.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.testingLogIn.WebsiteSecurityConfiguration.CustomUserDetailsService;
import com.example.testingLogIn.WebsiteSecurityConfiguration.UserModel;
import org.springframework.aop.AopInvocationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final ScheduleRepo scheduleRepo;
    private final StudentSubjectGradeLogsRepo subjGradeLogs;

    @Autowired
    public StudentSubjectGradeServices(StudentSubjectGradeRepo ssgRepo, SubjectRepo subjectRepo, sySemesterRepo semRepo, ScheduleRepo scheduleRepo, StudentSubjectGradeLogsRepo subjGradeLogs) {
        this.ssgRepo = ssgRepo;
        this.subjectRepo = subjectRepo;
        this.semRepo = semRepo;
        this.scheduleRepo = scheduleRepo;
        this.subjGradeLogs = subjGradeLogs;
    }

    public boolean didStudentPassed(int studentId, int gradeLevelId, int duration){
        try{
            if(duration == 1)
                return ssgRepo.didStudentPassed(studentId, gradeLevelId);

            for(SchoolYear sy : ssgRepo.findStudentUniqueAttendedSchoolYear(studentId, gradeLevelId)){
                if(ssgRepo.countPassedSemesters(studentId,gradeLevelId,sy.getSchoolYearNum()).size() >=2)
                    return true;
            }
            return false;
        }catch(AopInvocationException aie){
            return false;
        }
    }

    @Async
    public void addStudentGrades(Enrollment enrollment){
        subjectRepo.findActiveSubjectsNotDeletedByGradeLevel(enrollment.getGradeLevelToEnroll().getLevelId())
                        .forEach(subject ->{
                        StudentSubjectGrade studSubGrade = StudentSubjectGrade.builder()
                                                            .student(enrollment.getStudent())
                                                            .subject(subject)
                                                            .section(enrollment.getSectionToEnroll())
                                                            .semester(enrollment.getSYSemester())
                                                            .isNotDeleted(true)
                                                            .isDropped(false)
                                                            .subjectGrade(null)
                                                            .build();
                        ssgRepo.save(studSubGrade);
                    });
    }
    
    public List<StudentSubjectGradeDTO> getStudentsGradeBySectionSubject(int sectionId,int subjectId){
        UserModel currentUser = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            currentUser = (UserModel)authentication.getPrincipal();
        }

        if(currentUser != null && currentUser.getRole() != Role.ADMIN)
            if(scheduleRepo.findTeacherSectionSubjectSchedule(subjectId, sectionId, currentUser.getStaffId()).isEmpty())
                throw new IllegalArgumentException("You do not have this subject in your current teaching schedule. Please check your assigned subjects or contact the administrator for clarification.");

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
    
    public List<StudentGradesPerSem> getStudentGradesBySemester(int studentId){
        List<SchoolYearSemester> semAttended = ssgRepo.getStudentSemAttended(studentId);
        List<StudentGradesPerSem> studentGrades = new ArrayList<>();

        for(SchoolYearSemester sem : semAttended){
            StudentGradesPerSem semGrade = new StudentGradesPerSem();
            semGrade.setSectionSemester(sem.toString());
            semGrade.setGrades(ssgRepo.getGradesByStudent(studentId,sem.getSySemNumber())
                    .stream().map(StudentSubjectGrade::DTOmapper).toList());
            semGrade.setGradeSection(semGrade.getGrades().getFirst().getGradeAndSection());
            studentGrades.add(semGrade);
        }
        return studentGrades;
    }

    public boolean updateStudentGrade(StudentSubjectGradeDTO studGrade){
        StudentSubjectGrade studSubGrade = ssgRepo.findById(studGrade.getStudGradeId()).orElse(null);
        if(studSubGrade == null)
            return false;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            subjGradeLogs.save(new StudentSubjectGradeLogs(studSubGrade,(UserModel)auth.getPrincipal()));
            studSubGrade.setSubjectGrade(studGrade.getSubjectGrade());
            ssgRepo.save(studSubGrade);
            return true;
        }
        throw new IllegalArgumentException("Transaction Not Secured. Please Re-log in your user account");
    }

    public List<GradeLogsDTO> gradeLogs(int ssgId){
        return subjGradeLogs.getLogs(ssgId).stream()
                .map(logs -> new GradeLogsDTO(logs.getGradedBy().getFullName(),NonModelServices.getDateTimeFormatter().format(logs.getDateModified()))).toList();
    }
}
