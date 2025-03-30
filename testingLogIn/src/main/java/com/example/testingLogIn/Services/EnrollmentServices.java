package com.example.testingLogIn.Services;

import com.example.testingLogIn.AssociativeModels.GradeLevelRequiredFees;
import com.example.testingLogIn.AssociativeModels.StudentTransfereeRequirements;
import com.example.testingLogIn.CountersService.SectionStudentCountServices;
import com.example.testingLogIn.CustomObjects.EnrollmentHandler;
import com.example.testingLogIn.CustomObjects.StudentTotalDiscount;
import com.example.testingLogIn.Enums.EnrollmentStatus;
import com.example.testingLogIn.ModelDTO.EnrollmentDTO;
import com.example.testingLogIn.CustomObjects.EnrollmentPaymentView;
import com.example.testingLogIn.ModelDTO.StudentDTO;
import com.example.testingLogIn.Models.*;
import com.example.testingLogIn.PagedResponse.EnrollmentDTOPage;
import com.example.testingLogIn.Repositories.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
/**
 *
 * @author magno
 */
@Service
public class EnrollmentServices {
    private final EnrollmentRepo enrollmentRepo;
    private final StudentRepo studentRepo;
    private final SectionRepo sectionRepo;
    private final sySemesterRepo sySemRepo;
    private final GradeLevelRepo gradeLevelRepo;
    private final StudentSubjectGradeServices ssgService;
    private final GradeLevelRequiredFeeRepo gradelvlReqFeesRepo;
    private final PaymentsRecordRepo payRecRepo;
    private final StudentFeesListService studFeeListService;
    private final DiscountsServices discService;
    private final SectionStudentCountServices sscService;
    private final PaymentRecordService paymentService;
    private final TransfereeReqRepo transReqRepo;

    @Autowired
    public EnrollmentServices(EnrollmentRepo enrollmentRepo, StudentRepo studentRepo, SectionRepo sectionRepo, sySemesterRepo sySemRepo, GradeLevelRepo gradeLevelRepo, StudentSubjectGradeServices ssgService, GradeLevelRequiredFeeRepo gradelvlReqFeesRepo, PaymentsRecordRepo payRecRepo, StudentFeesListService studFeeListService, DiscountsServices discService, SectionStudentCountServices sscService, PaymentRecordService paymentService, TransfereeReqRepo transReqRepo) {
        this.enrollmentRepo = enrollmentRepo;
        this.studentRepo = studentRepo;
        this.sectionRepo = sectionRepo;
        this.sySemRepo = sySemRepo;
        this.gradeLevelRepo = gradeLevelRepo;
        this.ssgService = ssgService;
        this.gradelvlReqFeesRepo = gradelvlReqFeesRepo;
        this.payRecRepo = payRecRepo;
        this.studFeeListService = studFeeListService;
        this.discService = discService;
        this.sscService = sscService;
        this.paymentService = paymentService;
        this.transReqRepo = transReqRepo;
    }
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

    public boolean cancelEnrollment(int enrollmentId,boolean undoCancel){
        Enrollment enrollmentRecord = enrollmentRepo.findById(enrollmentId).orElseThrow(NullPointerException::new);
        enrollmentRecord.setNotDeleted(undoCancel);
        enrollmentRepo.save(enrollmentRecord);
        return true;
    }

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
            if (!student.isNew() && gradeLevelToEnroll.getPreRequisite() != null) {
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

            toAdd = toAdd - Math.ceil(((toAdd*std.getTotalPercentageDiscount())+std.getTotalFixedDiscount()));
            double newBalance=student.getStudentBalance() + toAdd;
            student.setStudentBalance(newBalance);
            student.setCurrentGradeSection(enrollmentRecord.getSectionToEnroll());
            studentRepo.save(student);

            CompletableFuture<Void> updateSection = CompletableFuture.runAsync(()-> sscService.updateSectionIfExist(enrollmentRecord));
            CompletableFuture<Void> updateStudent = CompletableFuture.runAsync(() -> studentRepo.save(student));
            CompletableFuture<Void> addStudentGrades = CompletableFuture.runAsync(() -> ssgService.addStudentGrades(enrollmentRecord));
            CompletableFuture<Void> addFeesRecord = CompletableFuture.runAsync(() -> studFeeListService.addFeesRecord(enrollmentRecord));
            CompletableFuture.allOf(updateStudent,addFeesRecord,addStudentGrades,updateSection).join();

            return 2;
        }
    }

    public EnrollmentDTOPage getAllEnrollmentPage(String status,Integer pageNo,Integer pageSize,String sort ,String search) {
        Pageable pageable;
        if(sort == null || sort.trim().isEmpty())
            pageable = PageRequest.of(pageNo-1,pageSize);
        else
            pageable = PageRequest.of(pageNo-1,pageSize,sortBy(sort));

        EnrollmentStatus estatus = getEnrollmentStatus(status);
        int sem = Optional.of(sySemRepo.findCurrentActive().getSySemNumber()).orElseThrow(NullPointerException::new);
        Page<EnrollmentHandler> enrollmentRetrieved = enrollmentRepo.testing(estatus,sem,search,pageable);
        List<EnrollmentDTO> pageContent = enrollmentRetrieved.getContent().stream().peek(
                e -> {
                    Student stud = e.getStudent();
                    if(stud.isTransferee() && stud.isNew()) {
                        List<TransfereeRequirements> compiled = stud.getTransfereeRequirements().stream().filter(StudentTransfereeRequirements::isNotDeleted).map(StudentTransfereeRequirements::getRequirement).toList();
                        for (TransfereeRequirements transfereeRequirements : transReqRepo.findByIsNotDeletedTrue()) {
                            if (!compiled.contains(transfereeRequirements)) {
                                stud.setLastGradeLevelCompleted(null);
                                break;
                            }
                        }
                    }
                    e.setStudent(stud);
                }
        ).map(enrollmentHandler -> new EnrollmentDTO(isComplete(enrollmentHandler.getEnrollment()),
                enrollmentHandler.getStudent().DTOmapper())).toList();
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
        return isComplete(enr);
    }

    public EnrollmentPaymentView getStudentPaymentStatus(int enrollmentId) {
        Enrollment er = enrollmentRepo.findById(enrollmentId).orElse(null);
        assert er != null;
        EnrollmentPaymentView epv = EnrollmentPaymentView.build(er);

        List<GradeLevelRequiredFees> gradeFeeList = gradelvlReqFeesRepo
                .findByGradeLevel(er.getGradeLevelToEnroll().getLevelId());
        StudentTotalDiscount std = discService.getStudentTotalDiscount(er.getStudent().getStudentId());
        int actvSemId = sySemRepo.findCurrentActive().getSySemNumber();
        gradeFeeList.forEach(reqFee -> {
                    RequiredFees toPay = reqFee.getRequiredFee();
                    double totalPaid = Optional.ofNullable(payRecRepo.getTotalPaidByStudentForFeeInSemester(er.getStudent().getStudentId(), toPay.getId(), actvSemId)).orElse(0.0);

                    double reqAmount = toPay.getRequiredAmount();
                    double discountedBalance = Math.ceil(reqAmount - ((reqAmount*std.getTotalPercentageDiscount()) + std.getTotalFixedDiscount()));
                    String status = totalPaid >= discountedBalance? "Fully Paid":
                                    totalPaid > 0 ?                 "Partially Paid":
                                                                    "Unpaid";
                    toPay.setRequiredAmount(discountedBalance);
                    epv.addNewFeeStatus(reqFee.getRequiredFee().getName(),discountedBalance,totalPaid,status);
                });
        return epv;
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

        boolean isComplete = true;
        try {
            List<GradeLevelRequiredFees> gradeFeeList = gradelvlReqFeesRepo
                    .findByGradeLevel(e.getGradeLevelToEnroll().getLevelId());
            for (GradeLevelRequiredFees gr : gradeFeeList) {
                Double result = Optional.ofNullable(payRecRepo.totalPaidForSpecificFee(e.getStudent().getStudentId(),
                        gr.getRequiredFee().getId(),
                        sySemRepo.findCurrentActive().getSySemNumber())).orElse(0.0);
                if (result == 0){
                    isComplete = false;
                    break;}
            }
            if (!isComplete && paymentService.getStudentPaymentForm(e.getStudent().getStudentId(), e.getGradeLevelToEnroll().getLevelId()).getTotalFee() == 0)
                isComplete = true;
        } catch (NullPointerException npe) {
            isComplete = false;
        }
        return e.DTOmapper(isComplete);
    }
}
