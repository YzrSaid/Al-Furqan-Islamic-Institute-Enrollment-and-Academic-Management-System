package com.example.testingLogIn.Services;

import com.example.testingLogIn.CustomObjects.StudentTotalDiscount;
import com.example.testingLogIn.Enums.EnrollmentStatus;
import com.example.testingLogIn.ModelDTO.EnrollmentDTO;
import com.example.testingLogIn.CustomObjects.EnrollmentPaymentView;
import com.example.testingLogIn.ModelDTO.StudentDTO;
import com.example.testingLogIn.Models.*;
import com.example.testingLogIn.PagedResponse.EnrollmentDTOPage;
import com.example.testingLogIn.Repositories.EnrollmentRepo;
import com.example.testingLogIn.Repositories.GradeLevelRepo;
import com.example.testingLogIn.Repositories.GradeLevelRequiredFeeRepo;
import com.example.testingLogIn.Repositories.PaymentsRecordRepo;
import com.example.testingLogIn.Repositories.SectionRepo;
import com.example.testingLogIn.Repositories.StudentRepo;
import com.example.testingLogIn.Repositories.sySemesterRepo;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Autowired
    private PaymentRecordService paymentService;

    @Autowired
    public EnrollmentServices(EnrollmentRepo enrollmentRepo, StudentRepo studentRepo, SectionRepo sectionRepo, sySemesterRepo sySemRepo, GradeLevelRepo gradeLevelRepo, StudentSubjectGradeServices ssgService, GradeLevelRequiredFeeRepo gradelvlReqFeesRepo, PaymentsRecordRepo payRecRepo, StudentFeesListService studFeeListService, DiscountsServices discService) {
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
    }

    public boolean addStudentToListing(StudentDTO stud, Integer studentId) {
        Student student = null;
        if (stud != null)
            student = studentRepo.findByName(stud.getFirstName(), stud.getLastName());
        else
            student = studentRepo.findById(studentId).orElse(null);

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

    public int addToAssessment(int enrollmentId, int gradeLevelId) {
        Enrollment enrollmentRecord = enrollmentRepo.findById(enrollmentId).orElse(null);
        GradeLevel gradeLevelToEnroll = gradeLevelRepo.findById(gradeLevelId).orElse(null);
        assert enrollmentRecord != null;
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
            Double toAdd=(gradelvlReqFeesRepo
                    .findTotalAmountByGradeLevel(enrollmentRecord.getGradeLevelToEnroll().getLevelId()));
            toAdd = toAdd - Math.ceil(((toAdd*std.getTotalPercentageDiscount())+std.getTotalFixedDiscount()));
            double newBalance=student.getStudentBalance() + (toAdd == null ? 0 :toAdd);
            student.setStudentBalance(newBalance);
            student.setCurrentGradeSection(enrollmentRecord.getSectionToEnroll());
            studentRepo.save(student);

            CompletableFuture<Void> updateStudent = CompletableFuture.runAsync(() -> studentRepo.save(student));
            CompletableFuture<Void> addStudentGrades = CompletableFuture.runAsync(() -> ssgService.addStudentGrades(enrollmentRecord));
            CompletableFuture<Void> addFeesRecord = CompletableFuture.runAsync(() -> studFeeListService.addFeesRecord(enrollmentRecord));
            CompletableFuture.allOf(updateStudent,addFeesRecord,addStudentGrades).join();

            return 2;
        }
    }

    public List<EnrollmentDTO> getAllEnrollment(String status) {
        EnrollmentStatus enrollmentStatus = getEnrollmentStatus(status);
        return enrollmentRepo.findRecordsByStatusAndSemester(
                enrollmentStatus,
                sySemRepo.findCurrentActive().getSySemNumber())
                .stream()
                .map(this::isComplete)
                .toList();
    }

    public EnrollmentDTOPage getAllEnrollmentPage(String status,Integer pageNo,Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo-1,pageSize);
        EnrollmentStatus estatus = getEnrollmentStatus(status);
        int sem = sySemRepo.findCurrentActive().getSySemNumber();

        Page<EnrollmentDTO> enrollmentPage = enrollmentRepo.findRecordsByStatusAndSemesterPage(estatus,sem,pageable).map(this::isComplete);
        return EnrollmentDTOPage.builder()
                .content(enrollmentPage.getContent())
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPages(enrollmentPage.getTotalPages())
                .totalElements(enrollmentPage.getTotalElements())
                .isLast(enrollmentPage.isLast())
                .build();
    }

    private EnrollmentStatus getEnrollmentStatus(String status) {
        if (status.equalsIgnoreCase("LISTING"))
            return null;
        else if (status.equalsIgnoreCase("ASSESSMENT"))
            return EnrollmentStatus.ASSESSMENT;
        else if (status.equalsIgnoreCase("PAYMENT"))
            return EnrollmentStatus.PAYMENT;
        else
            return EnrollmentStatus.ENROLLED;
    }

    public EnrollmentDTO getEnrollment(int enrollmentId) {
        Enrollment enr = enrollmentRepo.findById(enrollmentId).orElse(null);
        assert enr != null;
        return isComplete(enr);
    }

    public EnrollmentPaymentView getStudentPaymentStatus(int enrollmentId) {
        Enrollment er = enrollmentRepo.findById(enrollmentId).orElse(null);
        assert er != null;
        EnrollmentPaymentView epv = EnrollmentPaymentView.builder()
                .studentId(er.getStudent().getStudentId())
                .studentDisplayId(er.getStudent().getStudentDisplayId())
                .studentFirstName(er.getStudent().getFirstName())
                .studentLastName(er.getStudent().getLastName())
                .studentMiddleName(er.getStudent().getMiddleName())
                .feeStatus(new HashMap<>())
                .build();

        List<GradeLevelRequiredFees> gradeFeeList = gradelvlReqFeesRepo
                .findByGradeLevel(er.getGradeLevelToEnroll().getLevelId());
        StudentTotalDiscount std = discService.getStudentTotalDiscount(er.getStudent().getStudentId());
        int actvSemId = sySemRepo.findCurrentActive().getSySemNumber();
        gradeFeeList
                .forEach(reqFee -> {
                    RequiredFees toPay = reqFee.getRequiredFee();
                    double totalPaid = 0;
                    try {
                        totalPaid = payRecRepo.getTotalPaidByStudentForFeeInSemester(
                                er.getStudent().getStudentId(), toPay.getId(), actvSemId);
                    } catch (NullPointerException ignored) {
                        //nothing to do
                    }
                    double reqAmount = toPay.getRequiredAmount();
                    double discountedBalance = Math.ceil(reqAmount - ((reqAmount*std.getTotalPercentageDiscount()) + std.getTotalFixedDiscount()));
                    String status = totalPaid >= discountedBalance? "Fully Paid":
                                    totalPaid > 0 ?                 "Partially Paid":
                                                                    "Unpaid";
                    toPay.setRequiredAmount(discountedBalance);
                    epv.getFeeStatus().put(toPay, status);
                });
        return epv;
    }

    private EnrollmentDTO isComplete(Enrollment e) {
        boolean isComplete = true;
        try {
            List<GradeLevelRequiredFees> gradeFeeList = gradelvlReqFeesRepo
                    .findByGradeLevel(e.getGradeLevelToEnroll().getLevelId());
            for (GradeLevelRequiredFees gr : gradeFeeList) {
                Double result = payRecRepo.totalPaidForSpecificFee(e.getStudent().getStudentId(),
                        gr.getRequiredFee().getId(),
                        sySemRepo.findCurrentActive().getSySemNumber());
                if (result == null || result == 0)
                    isComplete = false;
            }
        } catch (NullPointerException npe) {
            isComplete = false;
        }
        try {
            if (!isComplete && paymentService.getStudentPaymentForm(e.getStudent().getStudentId(), e.getGradeLevelToEnroll().getLevelId()).getTotalFee() == 0)
                isComplete = true;
        }catch (NullPointerException npe){
            //do nothing
        }

        return e.DTOmapper(isComplete);
    }
}
