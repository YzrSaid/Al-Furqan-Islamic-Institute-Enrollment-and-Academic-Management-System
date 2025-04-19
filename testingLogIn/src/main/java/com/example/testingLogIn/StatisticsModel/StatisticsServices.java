package com.example.testingLogIn.StatisticsModel;

import com.example.testingLogIn.Enums.Semester;
import com.example.testingLogIn.Models.Enrollment;
import com.example.testingLogIn.Models.GradeLevel;
import com.example.testingLogIn.Models.SchoolYearSemester;
import com.example.testingLogIn.Repositories.GradeLevelRepo;
import com.example.testingLogIn.Repositories.StudentSubjectGradeRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StatisticsServices {
    private final EnrolledCountRepo enrolledCountRepo;
    private final GraduatesCountRepo graduatesCountRepo;
    private final GraduateStudentsRepo graduateStudentsRepo;
    private final PreEnrolledCountRepo preEnrolledCountRepo;
    private final RetainedCountRepo retainedCountRepo;
    private final PassedCountRepo passedCountRepo;
    private final GradeLevelEnrolledCountRepo gradeLevelEnrolledCountRepo;
    private final StudentPassingRecordRepo studentPassingRecordRepo;
    private final StudentSubjectGradeRepo studentSubjectGradeRepo;
    private final GradeLevelRetainedCountRepo gradeLevelRetainedCountRepo;
    private final GradeLevelPassedCountRepo gradeLevelPassedCountRepo;
    private final GradeLevelRepo gradeLevelRepo;

    @Autowired
    public StatisticsServices(EnrolledCountRepo enrolledCountRepo, GraduatesCountRepo graduatesCountRepo, GraduateStudentsRepo graduateStudentsRepo, PreEnrolledCountRepo preEnrolledCountRepo, RetainedCountRepo retainedCountRepo, PassedCountRepo passedCountRepo, GradeLevelEnrolledCountRepo gradeLevelEnrolledCountRepo, StudentPassingRecordRepo studentPassingRecordRepo, StudentSubjectGradeRepo studentSubjectGradeRepo, GradeLevelRetainedCountRepo gradeLevelRetainedCountRepo, GradeLevelPassedCountRepo gradeLevelPassedCountRepo, GradeLevelRepo gradeLevelRepo) {
        this.enrolledCountRepo = enrolledCountRepo;
        this.graduatesCountRepo = graduatesCountRepo;
        this.graduateStudentsRepo = graduateStudentsRepo;
        this.preEnrolledCountRepo = preEnrolledCountRepo;
        this.retainedCountRepo = retainedCountRepo;
        this.passedCountRepo = passedCountRepo;
        this.gradeLevelEnrolledCountRepo = gradeLevelEnrolledCountRepo;
        this.studentPassingRecordRepo = studentPassingRecordRepo;
        this.studentSubjectGradeRepo = studentSubjectGradeRepo;
        this.gradeLevelRetainedCountRepo = gradeLevelRetainedCountRepo;
        this.gradeLevelPassedCountRepo = gradeLevelPassedCountRepo;
        this.gradeLevelRepo = gradeLevelRepo;
    }

    public void setInitialCounts(SchoolYearSemester sem){
        int semId = sem.getSySemNumber();
        if(preEnrolledCountRepo.findBySemester(semId).orElse(null) == null)
            preEnrolledCountRepo.save(new PreEnrolledCount(sem));
        if(enrolledCountRepo.findBySemester(semId).orElse(null) == null)
            enrolledCountRepo.save(new EnrolledCount(sem));
        if(graduatesCountRepo.findBySemester(semId).orElse(null) == null)
            graduatesCountRepo.save(new GraduatesCount(sem));
        if(passedCountRepo.findBySemester(semId).orElse(null)==null)
            passedCountRepo.save(new PassedCount(0,sem));
        if(retainedCountRepo.findBySemester(semId).orElse(null)==null)
            retainedCountRepo.save(new RetainedCount(0,sem));
    }

    @Transactional
    public void updatePreEnrolledCount(SchoolYearSemester sem, boolean isDecrease){
        if(isDecrease)
            preEnrolledCountRepo.reduceByOne(sem.getSySemNumber());
        else
            preEnrolledCountRepo.addByOne(sem.getSySemNumber());
    }

    @Transactional
    public void updateEnrolledCount(Enrollment enrollment){
        SchoolYearSemester sem = enrollment.getSYSemester();
        GradeLevel gradeLevel = enrollment.getGradeLevelToEnroll();

        enrolledCountRepo.addByOne(sem.getSySemNumber());
        gradeLevelEnrolledCountRepo.findBySemesterAndGrade(sem.getSySemNumber(), gradeLevel.getLevelId())
                .ifPresentOrElse(
                        levelCount -> gradeLevelEnrolledCountRepo.increaseCount(sem.getSySemNumber(), gradeLevel.getLevelId()),
                        ()->gradeLevelEnrolledCountRepo.save(new GradeLevelEnrolledCount(sem,gradeLevel)));
    }

    public void setStudentPassingRecords(SchoolYearSemester sem){
        List<StudentPassingRecord> studentRetainedList = studentSubjectGradeRepo.findFailedStudents(sem.getSySemNumber(),null)
                                                        .stream().map(ssg -> new StudentPassingRecord(false,sem,ssg.getGradeLevel(),ssg.getStudent())).toList();
        List<StudentPassingRecord> studentPassedList = studentSubjectGradeRepo.findPassedStudents(sem.getSySemNumber(),null)
                .stream().map(ssg -> new StudentPassingRecord(true,sem,ssg.getGradeLevel(),ssg.getStudent())).toList();
        studentPassingRecordRepo.saveAll(studentPassedList);
        studentPassingRecordRepo.saveAll(studentRetainedList);
        System.out.println(studentRetainedList.size());
        retainedCountRepo.updateCount(sem.getSySemNumber(), studentRetainedList.size());
        System.out.println(studentPassedList.size());
        passedCountRepo.updateCount(sem.getSySemNumber(), studentPassedList.size());

        for(GradeLevel gradeLevel : gradeLevelRepo.findByIsNotDeletedTrue()){
            gradeLevelRetainedCountRepo.save(new GradeLevelRetainedCount(sem,gradeLevel,studentSubjectGradeRepo.findFailedStudents(sem.getSySemNumber(), gradeLevel.getLevelId()).size()));
            gradeLevelPassedCountRepo.save(new GradeLevelPassedCount(gradeLevel,sem,studentSubjectGradeRepo.findPassedStudents(sem.getSySemNumber(), gradeLevel.getLevelId()).size()));
        }
    }

    public CounterObject getCounts(Integer schoolYear, Semester semester){
        return CounterObject.builder()
                .enrolledCount(enrolledCountRepo.getSum(schoolYear,semester).orElse(0L))
                .preEnrolledCount(preEnrolledCountRepo.getSum(schoolYear,semester).orElse(0L))
                .graduatesCount(graduatesCountRepo.getSum(schoolYear,semester).orElse(0L))
                .passedCount(passedCountRepo.getSum(schoolYear,semester).orElse(0L))//good for when testing with actual development
                .retainedCount(retainedCountRepo.getSum(schoolYear, semester).orElse(0L))
                //.passedCount(gradeLevelPassedCountRepo.getTotal(schoolYear,semester,null).orElse(0L)) //good when testing by directly manipulating ang contents sa database
                //.retainedCount(gradeLevelRetainedCountRepo.getTotal(schoolYear,semester,null).orElse(0L))
                .build();
    }

    public GradeLevelRates getGradeLevelRates(Integer schoolYear, Semester semester, boolean didPassed){
        List<String> levelNames = new ArrayList<>();
        List<Long> rates = new ArrayList<>();
        for(GradeLevel gl : studentPassingRecordRepo.getUniqueGradeLevels(schoolYear, semester,didPassed)){
            levelNames.add(gl.getLevelName());
            int levelId = gl.getLevelId();
            if(didPassed)
                rates.add(gradeLevelPassedCountRepo.getTotal(schoolYear,semester,levelId).orElse(0L));
            else
                rates.add(gradeLevelRetainedCountRepo.getTotal(schoolYear,semester,levelId).orElse(0L));
        }
        return new GradeLevelRates(levelNames,rates);
    }
}
