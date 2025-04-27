package com.example.testingLogIn.StatisticsModel;

import com.example.testingLogIn.CustomObjects.PagedResponse;
import com.example.testingLogIn.Enums.EnrollmentStatus;
import com.example.testingLogIn.Enums.Semester;
import com.example.testingLogIn.ModelDTO.EnrollmentDTO;
import com.example.testingLogIn.Models.Enrollment;
import com.example.testingLogIn.Models.GradeLevel;
import com.example.testingLogIn.Models.SchoolYearSemester;
import com.example.testingLogIn.Models.Student;
import com.example.testingLogIn.Repositories.EnrollmentRepo;
import com.example.testingLogIn.Repositories.GradeLevelRepo;
import com.example.testingLogIn.Repositories.StudentSubjectGradeRepo;
import com.example.testingLogIn.Services.NonModelServices;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private final EnrollmentRepo enrollmentRepo;

    @Autowired
    public StatisticsServices(EnrolledCountRepo enrolledCountRepo, GraduatesCountRepo graduatesCountRepo, GraduateStudentsRepo graduateStudentsRepo, PreEnrolledCountRepo preEnrolledCountRepo, RetainedCountRepo retainedCountRepo, PassedCountRepo passedCountRepo, GradeLevelEnrolledCountRepo gradeLevelEnrolledCountRepo, StudentPassingRecordRepo studentPassingRecordRepo, StudentSubjectGradeRepo studentSubjectGradeRepo, GradeLevelRetainedCountRepo gradeLevelRetainedCountRepo, GradeLevelPassedCountRepo gradeLevelPassedCountRepo, GradeLevelRepo gradeLevelRepo, EnrollmentRepo enrollmentRepo) {
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
        this.enrollmentRepo = enrollmentRepo;
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

    public void setGraduatesInformation(List<Student> graduates, SchoolYearSemester sem){
        graduatesCountRepo.findBySemester(sem.getSySemNumber())
                .ifPresentOrElse(count -> graduatesCountRepo.updateCount(sem.getSySemNumber(), graduates.size()),
                        ()->graduatesCountRepo.save(new GraduatesCount(sem,graduates.size())));
        graduates.forEach(student -> graduateStudentsRepo.save(new GraduateStudents(sem,student)));
    }

    public void setStudentPassingRecords(SchoolYearSemester sem){
        List<StudentPassingRecord> studentRetainedList = studentSubjectGradeRepo.findFailedStudents(sem.getSySemNumber(),null)
                                                        .stream().map(ssg -> new StudentPassingRecord(false,sem,ssg.getSection(),ssg.getStudent(),ssg.getAverage())).toList();
        List<StudentPassingRecord> studentPassedList = studentSubjectGradeRepo.findPassedStudents(sem.getSySemNumber(),null)
                                                        .stream().map(ssg -> new StudentPassingRecord(true,sem,ssg.getSection(),ssg.getStudent(),ssg.getAverage())).toList();

        studentPassingRecordRepo.saveAll(studentPassedList);
        studentPassingRecordRepo.saveAll(studentRetainedList);
        retainedCountRepo.updateCount(sem.getSySemNumber(), studentRetainedList.size());
        passedCountRepo.updateCount(sem.getSySemNumber(), studentPassedList.size());

        for(GradeLevel gradeLevel : gradeLevelRepo.findByIsNotDeletedTrue()){
            gradeLevelRetainedCountRepo.save(new GradeLevelRetainedCount(sem,gradeLevel,studentSubjectGradeRepo.findFailedStudents(sem.getSySemNumber(), gradeLevel.getLevelId()).size()));
            gradeLevelPassedCountRepo.save(new GradeLevelPassedCount(gradeLevel,sem,studentSubjectGradeRepo.findPassedStudents(sem.getSySemNumber(), gradeLevel.getLevelId()).size()));
        }
    }

    public CounterObject getCounts(Integer schoolYear, Semester semester,String semKey){
        schoolYear = schoolYear == 0 ? null : schoolYear;
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

    public GradeLevelRates getGradeLevelRates(Integer schoolYear, Semester semester, boolean didPassed,String semKey, String passed){
        schoolYear = schoolYear == 0 ? null : schoolYear;
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

    public PagedResponse getEnrollmentStatistics(String search, String status, Integer sy,String sem, int pageNo, int pageSize){
        search = NonModelServices.forLikeOperator(search);
        EnrollmentStatus estatus = getEnrollmentStatus(status);
        sy = sy == 0 ? null : sy;
        Semester semester = getSemFromString(sem);
        Page<Enrollment> enrollmentPage =enrollmentRepo.getEnrollmentPageStatistics(search,estatus,sy,semester,PageRequest.of(pageNo-1,pageSize));
        List<EnrollmentDTO> getEnrollmentDTOs = enrollmentPage.getContent().stream().map(Enrollment::DTOmapper).toList();
        return PagedResponse.builder()
                .content(getEnrollmentDTOs)
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPages(enrollmentPage.getTotalPages())
                .totalElements(enrollmentPage.getTotalElements())
                .build();
    }

    public PagedResponse getGraduateStudents(String search, Integer sy, String sem, int pageNo, int pageSize){
        sy = sy == 0 ? null : sy;
        Semester semester = getSemFromString(sem);
        search = NonModelServices.forLikeOperator(search);

        Page<GraduateStudents> gradStudPage = graduateStudentsRepo.getGraduatesPage(search,sy,semester,PageRequest.of(pageNo - 1, pageSize));

        return PagedResponse.builder()
                .content(gradStudPage.getContent())
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalElements(gradStudPage.getTotalElements())
                .totalPages(gradStudPage.getTotalPages())
                .build();
    }

    public PagedResponse getStudentsPassingRecords(String search, Integer sy, String sem, int pageNo, int pageSize, Boolean didPassed, Integer levelId){

        search = NonModelServices.forLikeOperator(search);
        levelId = levelId == 0 ? null : levelId;
        sy = sy == 0 ? null : sy;
        Semester semester = getSemFromString(sem);

        Page<StudentPassingRecord> sprPage = studentPassingRecordRepo.getPassingRecords(search, sy, semester,didPassed,levelId,PageRequest.of(pageNo - 1, pageSize));

        return PagedResponse.builder()
                .content(sprPage.getContent())
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalElements(sprPage.getTotalElements())
                .totalPages(sprPage.getTotalPages())
                .build();
    }

    private Semester getSemFromString(String sem){
        return sem.equalsIgnoreCase("First") ? Semester.First :
                sem.equalsIgnoreCase("Second") ? Semester.Second:
                        null;
    }

    private EnrollmentStatus getEnrollmentStatus(String status) {
        if(status.equalsIgnoreCase("All"))
            return null;
        else if (status.equalsIgnoreCase("ASSESSMENT"))
            return EnrollmentStatus.ASSESSMENT;
        else if (status.equalsIgnoreCase("LISTING"))
            return EnrollmentStatus.LISTING;
        else if (status.equalsIgnoreCase("PAYMENT"))
            return EnrollmentStatus.PAYMENT;
        else if(status.equalsIgnoreCase("ENROLLED"))
            return EnrollmentStatus.ENROLLED;
        else
            return EnrollmentStatus.CANCELLED;
    }

}
