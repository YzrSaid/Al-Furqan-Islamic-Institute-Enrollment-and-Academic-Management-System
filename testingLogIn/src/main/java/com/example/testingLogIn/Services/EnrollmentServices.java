package com.example.testingLogIn.Services;

import com.example.testingLogIn.AssociativeModels.GradeLevelRequiredFees;
import com.example.testingLogIn.CountersService.SectionStudentCountServices;
import com.example.testingLogIn.CustomObjects.*;
import com.example.testingLogIn.Enums.EnrollmentStatus;
import com.example.testingLogIn.Enums.StudentStatus;
import com.example.testingLogIn.ModelDTO.EnrollmentDTO;
import com.example.testingLogIn.ModelDTO.StudentDTO;
import com.example.testingLogIn.Models.*;
import com.example.testingLogIn.PagedResponse.EnrollmentDTOPage;
import com.example.testingLogIn.Repositories.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class EnrollmentServices {
    private final EnrollmentRepo enrollmentRepo;
    private final StudentRepo studentRepo;
    private final SectionRepo sectionRepo;
    private final sySemesterRepo sySemRepo;
    private final GradeLevelRepo gradeLevelRepo;
    private final StudentSubjectGradeServices ssgService;
    private final GradeLevelRequiredFeeRepo gradelvlReqFeesRepo;
    private final StudentFeesListService studFeeListService;
    private final DiscountsServices discService;
    private final SectionStudentCountServices sscService;
    private final PaymentRecordService paymentService;
    private final DistributableServices distributableServices;
    private final PaymentRecordService paymentRecordService;

    @Autowired
    public EnrollmentServices(EnrollmentRepo enrollmentRepo, StudentRepo studentRepo, SectionRepo sectionRepo, sySemesterRepo sySemRepo, GradeLevelRepo gradeLevelRepo, StudentSubjectGradeServices ssgService, GradeLevelRequiredFeeRepo gradelvlReqFeesRepo,  StudentFeesListService studFeeListService, DiscountsServices discService, SectionStudentCountServices sscService, PaymentRecordService paymentService, DistributableServices distributableServices, PaymentRecordService paymentRecordService) {
        this.enrollmentRepo = enrollmentRepo;
        this.studentRepo = studentRepo;
        this.sectionRepo = sectionRepo;
        this.sySemRepo = sySemRepo;
        this.gradeLevelRepo = gradeLevelRepo;
        this.ssgService = ssgService;
        this.gradelvlReqFeesRepo = gradelvlReqFeesRepo;
        this.studFeeListService = studFeeListService;
        this.discService = discService;
        this.sscService = sscService;
        this.paymentService = paymentService;
        this.distributableServices = distributableServices;
        this.paymentRecordService = paymentRecordService;
    }

    @CacheEvict(value = "enrollmentPage",allEntries = true)
    public boolean addStudentToListing(Integer studentId) {
        Student student = null;
        if(studentId != null)
            student =  studentRepo.findById(studentId).orElse(null);

        if (student == null || !student.isNotDeleted())
            throw new NullPointerException();
        else if (enrollmentRepo.studentCurrentlyEnrolled(student.getStudentId(),
                sySemRepo.findCurrentActive().getSySemNumber()))
            return false;

        Enrollment enroll = new Enrollment();
        enroll.setEnrollmentStatus(EnrollmentStatus.LISTING);
        enroll.setStudent(student);
        enroll.setSYSemester(sySemRepo.findCurrentActive());
        enroll.setNotDeleted(true);
        enrollmentRepo.save(enroll);
        return true;
    }

    @CacheEvict(value = "enrollmentPage",allEntries = true)
    public boolean cancelEnrollment(int enrollmentId,boolean undoCancel){
        Enrollment enrollmentRecord = enrollmentRepo.findById(enrollmentId).orElseThrow(NullPointerException::new);
        enrollmentRecord.setNotDeleted(undoCancel);
        enrollmentRepo.save(enrollmentRecord);
        return true;
    }

    @CacheEvict(value = "enrollmentPage",allEntries = true)
    public int addToAssessment(int enrollmentId, int gradeLevelId) {
        Enrollment enrollmentRecord = enrollmentRepo.findById(enrollmentId).orElse(null);
        GradeLevel gradeLevelToEnroll = gradeLevelRepo.findById(gradeLevelId).orElse(null);
        Optional.ofNullable(enrollmentRecord).orElseThrow(NullPointerException::new);
        Student student = enrollmentRecord.getStudent();
        if (!enrollmentRecord.isNotDeleted())
            return 1;
        else if (gradeLevelToEnroll == null)
            return 2;
        else {
            enrollmentRecord.setEnrollmentStatus(EnrollmentStatus.ASSESSMENT);
            enrollmentRecord.setGradeLevelToEnroll(gradeLevelToEnroll);
            enrollmentRecord.setRemarks(null);
            boolean isQualified = true;
            if (!(student.getStatus() == StudentStatus.NEW) && gradeLevelToEnroll.getPreRequisite() != null) {
                int nextLevelPreReqId = gradeLevelToEnroll.getPreRequisite().getLevelId();
                isQualified = ssgService.didStudentPassed(student.getStudentId(),
                        nextLevelPreReqId);

                if (student.getCurrentGradeSection().getLevel().getLevelId() == gradeLevelToEnroll.getLevelId())
                    isQualified = true;
                if ((!isQualified) && student.getCurrentGradeSection().getLevel().getLevelId() == nextLevelPreReqId) {
                    enrollmentRecord.setRemarks("Congratulation! You Are Qualified To Your Current Grade");
                } else if (!isQualified)
                    enrollmentRecord.setRemarks(
                            "The Chosen Grade Level is Not The Successor of the Student's Current Grade Level. Choose another!");
                else
                    enrollmentRecord.setRemarks("Pagbayad na para maenroll naka");
            }
            enrollmentRecord.setComplete(false);
            enrollmentRecord.setQualified(isQualified);
            enrollmentRepo.save(enrollmentRecord);

            return 3;
        }
    }

    @CacheEvict(value = "enrollmentPage",allEntries = true)
    public int addToPayment(int enrollmentId, int sectionNumber) {
        Enrollment enrollmentRecord = enrollmentRepo.findById(enrollmentId).orElse(null);
        Section section = sectionRepo.findById(sectionNumber).orElse(null);

        if (enrollmentRecord == null || !enrollmentRecord.isNotDeleted())
            return 1;
        else if (section == null)
            return 2;
        else {
            enrollmentRecord.setEnrollmentStatus(EnrollmentStatus.PAYMENT);
            enrollmentRecord.setSectionToEnroll(section);
            enrollmentRepo.save(enrollmentRecord);

            return 3;
        }
    }

    @CacheEvict(value = "enrollmentPage",allEntries = true)
    public int addToEnrolled(int enrollmentId) {
        Enrollment enrollmentRecord = enrollmentRepo.findById(enrollmentId).orElse(null);
        if (enrollmentRecord == null || !enrollmentRecord.isNotDeleted())
            return 1;
        else {
            StudentTotalDiscount std = discService.getStudentTotalDiscount(enrollmentRecord.getStudent().getStudentId());
            enrollmentRecord.setEnrollmentStatus(EnrollmentStatus.ENROLLED);
            enrollmentRepo.save(enrollmentRecord);

            Student student = enrollmentRecord.getStudent();
            assert student != null;
            student.setNew(false);
            double toAdd= Optional.ofNullable(gradelvlReqFeesRepo
                    .findTotalAmountByGradeLevel(enrollmentRecord.getGradeLevelToEnroll().getLevelId())).orElse(0.0) ;

            toAdd = toAdd - NonModelServices.adjustDecimal((toAdd*std.getTotalPercentageDiscount())+std.getTotalFixedDiscount());
            double newBalance=student.getStudentBalance() + toAdd;
            student.setStudentBalance(newBalance);
            student.setCurrentGradeSection(enrollmentRecord.getSectionToEnroll());
            studentRepo.save(student);

            CompletableFuture<Void> updateSection = CompletableFuture.runAsync(()-> sscService.updateSectionIfExist(enrollmentRecord));
            CompletableFuture<Void> updateStudent = CompletableFuture.runAsync(() -> studentRepo.save(student));
            CompletableFuture<Void> addStudentGrades = CompletableFuture.runAsync(() -> ssgService.addStudentGrades(enrollmentRecord));
            CompletableFuture<Void> addFeesRecord = CompletableFuture.runAsync(() -> studFeeListService.addFeesRecord(enrollmentRecord));
            CompletableFuture<Void> setItemToReceive = CompletableFuture.runAsync(() -> distributableServices.setStudentItemToReceive(enrollmentRecord));
            CompletableFuture.allOf(updateStudent,addFeesRecord,addStudentGrades,setItemToReceive,updateSection).join();

            return 2;
        }
    }


    @Cacheable(
            value = "enrollmentPage",
            key = "#status + #pageNo + #pageSize",  // status becomes part of the cache key
            condition = "(#search == null || #search.isEmpty())"
    )
    public EnrollmentDTOPage getAllEnrollmentPage(String status,Integer pageNo,Integer pageSize,String sort ,String search) {
        Pageable pageable;
        if(sort == null || sort.trim().isEmpty())
            pageable = PageRequest.of(pageNo-1,pageSize);
        else
            pageable = PageRequest.of(pageNo-1,pageSize,sortBy(sort));

        EnrollmentStatus estatus = getEnrollmentStatus(status);
        int sem = Optional.of(sySemRepo.findCurrentActive().getSySemNumber()).orElseThrow(NullPointerException::new);
        Page<EnrollmentHandler> enrollmentRetrieved = enrollmentRepo.findStudentsEnrollment(estatus,sem,search,pageable);
        List<EnrollmentDTO> pageContent = enrollmentRetrieved.getContent().stream().map(enrollmentHandler -> {
            Enrollment enrollment = enrollmentHandler.getEnrollment();
            StudentDTO student = Optional.ofNullable(enrollmentHandler.getStudent()).map(Student::DTOmapper).orElse(null);
            if(enrollment != null){
                if(enrollment.getEnrollmentStatus().equals(EnrollmentStatus.PAYMENT))
                    return new EnrollmentDTO(isComplete(enrollment),student);

                return new EnrollmentDTO(enrollment.DTOmapper(),student);
            }
            return new EnrollmentDTO(null,student);
        }).toList();
        return EnrollmentDTOPage.buildMe(enrollmentRetrieved,pageContent);
    }

    private Sort sortBy(String sort){
        if(sort.equalsIgnoreCase("GradeLevel"))
            return Sort.by("e.gradeLevelToEnroll").ascending();
        else if(sort.equalsIgnoreCase("StudentName"))
            return Sort.by("stud.firstName").ascending();
        else
            return Sort.by("stud.studentDisplayId").ascending();

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
        else if(status.equalsIgnoreCase("NOT_REGISTERED"))
            return EnrollmentStatus.NOT_REGISTERED;
        else
            return EnrollmentStatus.CANCELLED;
    }

    public EnrollmentDTO getEnrollment(int enrollmentId) {
        Enrollment enr = enrollmentRepo.findById(enrollmentId).orElse(null);
        assert enr != null;
        return enr.getEnrollmentStatus().equals(EnrollmentStatus.PAYMENT) ? isComplete(enr) : enr.DTOmapper();
    }

    public StudentPaymentForm getStudentPaymentStatus(int enrollmentId) {
        Enrollment er = enrollmentRepo.findById(enrollmentId).orElse(null);
        assert er != null;
        return paymentRecordService.getStudentPaymentForm(er.getStudent().getStudentId(),er.getGradeLevelToEnroll().getLevelId());
    }

    public List<StudentDTO> getEnrolledStudentsBySection(int sectionId){
        int actvSemId = Optional.ofNullable( sySemRepo.findCurrentActive()).map(SchoolYearSemester::getSySemNumber).orElse(0);
        return enrollmentRepo.countCurrentlyEnrolled(sectionId,actvSemId).stream()
                .map(Student::DTOmapper)
                .collect(Collectors.toList());
    }

    private EnrollmentDTO isComplete(Enrollment e) {
        if(e == null)
            return null;
        StudentPaymentForm toPay = paymentService.getStudentPaymentForm(e.getStudent().getStudentId(), e.getGradeLevelToEnroll().getLevelId());
        boolean isComplete = true;
        for(FeesAndBalance fee : toPay.getFeesAndBalance()){
            if(fee.getTotalPaid() == 0){
                isComplete = false;
                break;
            }
        }
        return e.DTOmapper(isComplete);
    }
}
