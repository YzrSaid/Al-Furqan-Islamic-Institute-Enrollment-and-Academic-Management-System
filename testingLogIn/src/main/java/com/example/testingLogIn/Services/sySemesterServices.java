package com.example.testingLogIn.Services;

import com.example.testingLogIn.Enums.Semester;
import com.example.testingLogIn.Enums.StudentStatus;
import com.example.testingLogIn.Models.GradeLevel;
import com.example.testingLogIn.Models.SchoolYear;
import com.example.testingLogIn.Models.SchoolYearSemester;
import com.example.testingLogIn.Models.Student;
import com.example.testingLogIn.Repositories.*;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.testingLogIn.StatisticsModel.StatisticsServices;
import com.example.testingLogIn.WebsiteConfiguration.SchoolProfile;
import com.example.testingLogIn.WebsiteConfiguration.SchoolProfileRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
/**
 *
 * @author magno
 */
@Service
public class sySemesterServices {
    private final sySemesterRepo semesterRepo;
    private final SchoolYearRepo syRepo;
    private final RequiredPaymentsRepo reqFee;
    private final StudentRepo studentRepo;
    private final SchoolProfileRepo schoolProfileRepo;
    private final StudentSubjectGradeRepo ssgr;
    private final SubjectRepo subjectRepo;
    private final StatisticsServices statisticsServices;

    @Autowired
    public sySemesterServices(sySemesterRepo semesterRepo, SchoolYearRepo syRepo, RequiredPaymentsRepo reqFee, StudentRepo studentRepo, SchoolProfileRepo schoolProfileRepo, StudentSubjectGradeRepo ssgr, SubjectRepo subjectRepo, StatisticsServices statisticsServices) {
        this.semesterRepo = semesterRepo;
        this.syRepo = syRepo;
        this.reqFee = reqFee;
        this.studentRepo = studentRepo;
        this.schoolProfileRepo = schoolProfileRepo;
        this.ssgr = ssgr;
        this.subjectRepo = subjectRepo;
        this.statisticsServices = statisticsServices;
    }

    public void addSemesters(String schoolYearName){
        SchoolYear sy = syRepo.getByName(schoolYearName);
        if(sy != null){
            int count = 0;
            while(count<2){
                Semester sem = count++ == 1? Semester.First : Semester.Second;
                
                SchoolYearSemester sySem = new SchoolYearSemester();
                sySem.setNotDeleted(true);
                sySem.setActive(false);
                sySem.setFinished(false);
                sySem.setEnrollmentDeadline(null);
                sySem.setSchoolYear(sy);
                sySem.setSem(sem);
                
                semesterRepo.save(sySem);
            }
        }
    }
    
    public SchoolYearSemester getCurrentActive(){
        return semesterRepo.findCurrentActive();
    }
    
    public Map<String,List<SchoolYearSemester>> getAllSemesters(){
        Map<String,List<SchoolYearSemester>> toReturn = new HashMap<>();
        List<SchoolYearSemester> allSem = semesterRepo.findAll().stream()
                           .filter(sem -> sem.getSchoolYear().isNotDeleted())
                           .sorted(Comparator.comparing((SchoolYearSemester sem) -> sem.getSchoolYear().getSchoolYear(), Comparator.reverseOrder())
                                                    .thenComparing(SchoolYearSemester::getSem))
                           .toList();
        
        allSem.forEach(sem -> {
                String schoolYearName = sem.getSchoolYear().getSchoolYear();
            if(!toReturn.containsKey(schoolYearName)){
                List<SchoolYearSemester> newSem = new ArrayList<>();
                toReturn.put(schoolYearName,newSem);
            }
            toReturn.get(schoolYearName).add(sem);
        });
        
        return toReturn;
    }
    @CacheEvict(value = {"enrollmentPage"},allEntries = true)
    public boolean activateSemester(int semNumber){
        SchoolYearSemester sem = semesterRepo.findById(semNumber).orElse(null);
        if(sem == null)
            return false;
        
        disableAll();
        sem.setEnrollmentDeadline(LocalDate.now().plusDays(30));
        sem.setActive(true);
        semesterRepo.save(sem);

        ExecutorService dbExecutor = Executors.newFixedThreadPool(4);

        CompletableFuture.runAsync(()->{
            reqFee.setRequiredFeesActive();
            subjectRepo.activeAll();
            studentRepo.setNewStudentsToOld();
            statisticsServices.setEnrolledCount(sem);
            statisticsServices.setPreEnrolledCount(sem);
        },dbExecutor).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
        
        return true;
    }

    @CacheEvict(value = {"enrollmentPage"},allEntries = true)
    public boolean deactivateSemester(int semNumber){
        SchoolYearSemester sem = semesterRepo.findById(semNumber).orElse(null);
        if(sem == null)
            return false;
        
        sem.setActive(false);
        semesterRepo.save(sem);
        
        return true;
    }
    @CacheEvict(value = {"enrollmentPage"},allEntries = true)
    public boolean finishSemester(int semNumber){
        SchoolYearSemester sem = semesterRepo.findById(semNumber).orElse(null);
        if(sem == null)
            return false;
        
        sem.setActive(false);
        sem.setFinished(true);
        semesterRepo.save(sem);

        ExecutorService dbExecutor = Executors.newFixedThreadPool(4);

        GradeLevel graduateLevel = graduatingLevel();
        if(graduateLevel != null){
            CompletableFuture.runAsync(()->{
                List<Student> students = studentRepo.findStudentsByCurrentGradeLevel(graduateLevel.getLevelId());
                students.forEach(student -> {
                    if(ssgr.didStudentPassed(student.getStudentId(), graduateLevel.getLevelId())){
                        student.setStatus(StudentStatus.GRADUATE);
                        student.setDataGraduated(LocalDate.now());
                    }
                });
                studentRepo.saveAll(students);
            },dbExecutor).exceptionally(ex -> {
                return null;
            });;
        }
        return true;
    }
    
    private void disableAll(){
            semesterRepo
                .findAll().forEach(sem -> {
                    sem.setActive(false);
                    semesterRepo.save(sem);
                });
    }

    private GradeLevel graduatingLevel() {
        try{
            SchoolProfile graduateGradeLevel = schoolProfileRepo.findById("GraduatingLevel").orElseThrow(NullPointerException::new);
            ByteArrayInputStream bais = new ByteArrayInputStream(graduateGradeLevel.getKey_value());
            ObjectInputStream ois = new ObjectInputStream(bais);
            GradeLevel toReturn = (GradeLevel) ois.readObject();
            bais.close();
            ois.close();
            return toReturn;
        } catch (Exception e){
            return null;
        }
    }
}
