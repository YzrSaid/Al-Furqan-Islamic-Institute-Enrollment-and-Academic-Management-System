package com.example.testingLogIn.StatisticsModel;

import com.example.testingLogIn.Models.SchoolYearSemester;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StatisticsServices {
    private final EnrolledCountRepo enrolledCountRepo;
    private final GraduatesCountRepo graduatesCountRepo;
    private final GraduateStudentsRepo graduateStudentsRepo;
    private final PreEnrolledCountRepo preEnrolledCountRepo;
    private final RetainedCountRepo retainedCountRepo;
    private final SectionStudentCountRepo sectionStudentCountRepo;

    @Autowired
    public StatisticsServices(EnrolledCountRepo enrolledCountRepo, GraduatesCountRepo graduatesCountRepo, GraduateStudentsRepo graduateStudentsRepo, PreEnrolledCountRepo preEnrolledCountRepo, RetainedCountRepo retainedCountRepo, SectionStudentCountRepo sectionStudentCountRepo) {
        this.enrolledCountRepo = enrolledCountRepo;
        this.graduatesCountRepo = graduatesCountRepo;
        this.graduateStudentsRepo = graduateStudentsRepo;
        this.preEnrolledCountRepo = preEnrolledCountRepo;
        this.retainedCountRepo = retainedCountRepo;
        this.sectionStudentCountRepo = sectionStudentCountRepo;
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
    public void updateEnrolledCount(SchoolYearSemester sem){
        enrolledCountRepo.addByOne(sem.getSySemNumber());
    }
}
