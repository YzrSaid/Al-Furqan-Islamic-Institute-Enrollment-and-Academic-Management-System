package com.example.testingLogIn.StatisticsModel;

import com.example.testingLogIn.Models.Enrollment;
import com.example.testingLogIn.Models.GradeLevel;
import com.example.testingLogIn.Models.SchoolYearSemester;
import com.example.testingLogIn.Repositories.GradeLevelRepo;
import com.example.testingLogIn.Repositories.StudentSubjectGradeRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public void setPreEnrolledCount(SchoolYearSemester sem){
        if(preEnrolledCountRepo.findBySemester(sem.getSySemNumber()).orElse(null) == null)
            preEnrolledCountRepo.save(new PreEnrolledCount(0,sem));
    }

    @Transactional
    public void updatePreEnrolledCount(SchoolYearSemester sem, boolean isDecrease){
        if(isDecrease)
            preEnrolledCountRepo.reduceByOne(sem.getSySemNumber());
        else
            preEnrolledCountRepo.addByOne(sem.getSySemNumber());
    }

    public void setEnrolledCount(SchoolYearSemester sem){
        if(enrolledCountRepo.findBySemester(sem.getSySemNumber()).orElse(null) == null)
            enrolledCountRepo.save(new EnrolledCount(0,sem));
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
        retainedCountRepo.save(new RetainedCount(studentRetainedList.size(),sem));
        passedCountRepo.save(new PassedCount(studentPassedList.size(),sem));

        for(GradeLevel gradeLevel : gradeLevelRepo.findByIsNotDeletedTrue()){
            gradeLevelRetainedCountRepo.save(new GradeLevelRetainedCount(sem,gradeLevel,studentSubjectGradeRepo.findFailedStudents(sem.getSySemNumber(), gradeLevel.getLevelId()).size()));
            gradeLevelPassedCountRepo.save(new GradeLevelPassedCount(gradeLevel,sem,studentSubjectGradeRepo.findPassedStudents(sem.getSySemNumber(), gradeLevel.getLevelId()).size()));
        }
    }
}
