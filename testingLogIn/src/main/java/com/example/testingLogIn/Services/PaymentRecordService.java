package com.example.testingLogIn.Services;

import com.example.testingLogIn.CustomObjects.FeesAndBalance;
import com.example.testingLogIn.CustomObjects.StudentPaymentForm;
import com.example.testingLogIn.CustomObjects.StudentTotalDiscount;
import com.example.testingLogIn.ModelDTO.PaymentRecordDTO;
import com.example.testingLogIn.CustomObjects.TotalPaid;
import com.example.testingLogIn.Models.*;
import com.example.testingLogIn.Repositories.*;
import com.example.testingLogIn.WebsiteSecurityConfiguration.CustomUserDetailsService;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;

@Data
@Builder
class MapperObject{
    private RequiredFees requiredFees;
    private double totalBalance;
}

/**
 *
 * @author magno
 */
@Service
public class PaymentRecordService {
    private final PaymentsRecordRepo paymentRepo;
    private final StudentRepo studentRepo;
    private final RequiredPaymentsRepo reqPaymentsRepo;
    private final sySemesterRepo SYSemRepo;
    private final CustomUserDetailsService userService;
    private final GradeLevelRequiredFeeRepo gradeReqFee;
    private final StudentFeesListRepo studFeesRepo;
    @Autowired
    private DiscountsServices discService;

    @Setter
    @Getter
    private double TheAmount = 0;

    @Autowired
    private PagedResourcesAssembler<PaymentRecordDTO> pagedResourcesAssembler;

    @Autowired
    public PaymentRecordService(PaymentsRecordRepo paymentRepo, StudentRepo studentRepo, RequiredPaymentsRepo reqPaymentsRepo,
                                sySemesterRepo SYSemRepo, CustomUserDetailsService userService, GradeLevelRequiredFeeRepo gradeReqFee,
                                StudentFeesListRepo studFeesRepo) {
        this.paymentRepo = paymentRepo;
        this.studentRepo = studentRepo;
        this.reqPaymentsRepo = reqPaymentsRepo;
        this.SYSemRepo = SYSemRepo;
        this.userService = userService;
        this.gradeReqFee = gradeReqFee;
        this.studFeesRepo = studFeesRepo;
    }

    //For deletion after new form update
    public boolean addNewRecord(PaymentRecordDTO paymentRec){
        Student student = studentRepo.findById(paymentRec.getStudId()).orElse(null);
        if(student == null)
            return false;
        PaymentRecords newPaymentRecord = PaymentRecords.builder()
                                                    .student(student)
                                                    .receiver(userService.getCurrentlyLoggedInUser())
                                                    .requiredPayment(reqPaymentsRepo.findById(paymentRec.getRequiredPaymentId()).orElse(null))
                                                    .SYSem(SYSemRepo.findCurrentActive())
                                                    .amount(paymentRec.getAmount())
                                                    .build();
        paymentRepo.save(newPaymentRecord);
        student.setStudentBalance(student.getStudentBalance()-paymentRec.getAmount());
        studentRepo.save(student);
        return true;
    }
    //NEW WAY OF PAYING
    public boolean addPaymentAutoAllocate(int studentId, Integer gradeLevelId,double amount,List<Integer> feesId){
        Student student = studentRepo.findById(studentId).orElse(null);
        assert student!=null;
        SchoolYearSemester sem = SYSemRepo.findCurrentActive();
        List<MapperObject> toSortByBalance = new ArrayList<>();
        setTheAmount(amount);
        StudentTotalDiscount std = discService.getStudentTotalDiscount(studentId);

        if(gradeLevelId != null) {//during enrollment
            for (GradeLevelRequiredFees reqFee : gradeReqFee.findByGradeLevel(gradeLevelId)) {
                if(feesId.contains(reqFee.getRequiredFee().getId())) {
//                    Optional.ofNullable(paymentRepo.totalPaidForSpecificFee(studentId, reqFee.getRequiredFee().getId(), sem.getSySemNumber())).orElse(0.0); uncomment if magka error hahahha
//                    Double result = paymentRepo.totalPaidForSpecificFee(studentId, reqFee.getRequiredFee().getId(), sem.getSySemNumber());
                    double paidAmount = Optional.ofNullable(paymentRepo.totalPaidForSpecificFee(studentId, reqFee.getRequiredFee().getId(), sem.getSySemNumber())).orElse(0.0);

                    double initialAmount = reqFee.getRequiredFee().getRequiredAmount();//initial amount deducted by the discount
                    double discountedAmount = initialAmount-((initialAmount * std.getTotalPercentageDiscount())+std.getTotalFixedDiscount());
                    double totalFeeBalance = discountedAmount - paidAmount;
                    if (totalFeeBalance > 0)
                        toSortByBalance.add(MapperObject.builder()
                                .requiredFees(reqFee.getRequiredFee())
                                .totalBalance(totalFeeBalance)
                                .build());
                }
            }
        }else {//all time fees
            for(StudentFeesList paid : studFeesRepo.findUniqueFees(studentId)){
                if(feesId.contains(paid.getFee().getId())) {
                    Double amountPaid = paymentRepo.totalPaidForSpecificFee(studentId,paid.getFee().getId(),null);
                    amountPaid = amountPaid != null ? amountPaid : 0;
                    double remainingBalance = Optional.ofNullable(studFeesRepo.totalPerFeesByStudent(studentId, paid.getFee().getId())).orElse(0d) - amountPaid;
                    if (remainingBalance > 0)
                        toSortByBalance.add(MapperObject.builder()
                                .requiredFees(paid.getFee())
                                .totalBalance(remainingBalance)
                                .build());
                }
            }
        }
        AtomicInteger count = new AtomicInteger(1);
        toSortByBalance = toSortByBalance.stream()
                .sorted(Comparator.comparingDouble(MapperObject::getTotalBalance))
                            .collect(Collectors.toList());
        double average = getTheAmount()/toSortByBalance.size();
        int size = toSortByBalance.size();

        toSortByBalance.forEach(fee -> {
            double toAllocate = 0;
            if(count.get() != size)
                toAllocate = Math.min(average,fee.getTotalBalance());
            else
                toAllocate = getTheAmount();
            setTheAmount(getTheAmount()-toAllocate);
            count.getAndIncrement();

            paymentRepo.save(PaymentRecords.builder().student(student)
                            .requiredPayment(fee.getRequiredFees())
                            .amount(toAllocate)
                            .SYSem(sem)
                            .receiver(userService.getCurrentlyLoggedInUser())
                            .build());
        });
        student.setStudentBalance(student.getStudentBalance()-amount);
        studentRepo.save(student);
        return true;
    }

    public boolean editRecord(int recordId, int feeId){
        PaymentRecords record = paymentRepo.findById(recordId).orElse(null);
        if(record == null)
            return false;
        record.setRequiredPayment(reqPaymentsRepo.findById(feeId).orElse(null));
        paymentRepo.save(record);
        return true;
    }
    //To be removed next student payment form update
    public double getStudentBalance(int studentId,Integer feeId,Integer gradeLevelId,Boolean isCurrentSem){
        Student student = studentRepo.findById(studentId).orElse(null);
        SchoolYearSemester sem = SYSemRepo.findCurrentActive();
        assert student != null;
        if(feeId == null) {
            //for enrollment part
            if (isCurrentSem != null && isCurrentSem) {
                double toReturn = 0;
                for (GradeLevelRequiredFees reqFee : gradeReqFee.findByGradeLevel(gradeLevelId)) {
                    Double result = paymentRepo.totalPaidForSpecificFee(studentId, reqFee.getRequiredFee().getId(), sem.getSySemNumber());
                    double paidAmount = result != null ? result : 0;
                    double totalFeeBalance = reqFee.getRequiredFee().getRequiredAmount() - paidAmount;
                    toReturn += totalFeeBalance;
                }
                return toReturn;
            }
            return student.getStudentBalance();
        }else{
            Integer semId = isCurrentSem != null ? SYSemRepo.findCurrentActive().getSySemNumber() : null;//to remove
            RequiredFees reqFee = reqPaymentsRepo.findById(feeId).orElse(null);//to remove
            assert reqFee != null;
            Double amountPaid = paymentRepo.totalPaidForSpecificFee(studentId,feeId,semId);
            double totalPaidAmount = amountPaid == null ? 0 : amountPaid;//(studentId,feeId,null)
            double totalFeeBalance = isCurrentSem != null ? reqFee.getRequiredAmount() : studFeesRepo.totalPerFeesByStudent(studentId,feeId);
            return totalFeeBalance-totalPaidAmount;
        }
    }

    //A payment form object
    public StudentPaymentForm getStudentPaymentForm(int studentId,  Integer gradeLevelId){
        Student student = studentRepo.findById(studentId).orElse(null);
        SchoolYearSemester sem = SYSemRepo.findCurrentActive();
        Map<RequiredFees,Double> feesBalance = new HashMap<>();
        assert student != null;
        StudentPaymentForm studentPaymentForm = StudentPaymentForm.builder()
                                                    .totalFee(0.0d)
                                                    .student(student.DTOmapper())
                                                    .feesAndBalance(new ArrayList<>())
                                                    .build();
        double totalBalance = 0;
        //for enrollment
        if(gradeLevelId != null){
            StudentTotalDiscount std = discService.getStudentTotalDiscount(studentId);
            for (GradeLevelRequiredFees reqFee : gradeReqFee.findByGradeLevel(gradeLevelId)) {
                double paidAmount = Optional.ofNullable(paymentRepo.totalPaidForSpecificFee(studentId, reqFee.getRequiredFee().getId(), sem.getSySemNumber())).orElse(0.0d);
                double initialAmount = reqFee.getRequiredFee().getRequiredAmount();
                double discountedAmount = Math.ceil(initialAmount - ((initialAmount*std.getTotalPercentageDiscount())-std.getTotalFixedDiscount()));
                double remainingBalance = discountedAmount - paidAmount;
                if(remainingBalance>0){
                    studentPaymentForm.getFeesAndBalance().add(new FeesAndBalance(reqFee.getRequiredFee(),remainingBalance));
                    totalBalance+=remainingBalance;}
            }
        }else{//For all time debt
            for(StudentFeesList sfl : studFeesRepo.findBySem(studentId,null)) {
                if (!feesBalance.containsValue(sfl.getFee())) {
                    double totalPaidAmount = paymentRepo.totalPaidForSpecificFee(studentId, sfl.getFee().getId(), null);//(studentId,feeId,null)
                    double totalFeeBalance = studFeesRepo.totalPerFeesByStudent(studentId, sfl.getFee().getId());
                    double remainingBalance = Math.ceil(totalFeeBalance - totalPaidAmount);
                    if (remainingBalance > 0) {
                        totalBalance += remainingBalance;
                        studentPaymentForm.getFeesAndBalance().add(new FeesAndBalance(sfl.getFee(),remainingBalance));
                    }
                }
            }
        }
        studentPaymentForm.setTotalFee(totalBalance);
        return studentPaymentForm;
    }
    
    public List<PaymentRecordDTO> getAllPaymentRecords(){
        return paymentRepo.getAllRecords().stream()
                        .map(PaymentRecords::DTOmapper)
                        .toList();
    }
    
    public List<PaymentRecordDTO> getAllPaymentRecordsBySem(int semId){
        return paymentRepo.getRecordsBySem(semId).stream()
                        .map(PaymentRecords::DTOmapper)
                        .toList();
    }

    public List<PaymentRecordDTO> getAllTimeRecordsByDates(String condition){
        Sort sort = condition.equalsIgnoreCase("desc") ?
                Sort.by(Sort.Order.desc("datePaid")) :
                Sort.by(Sort.Order.asc("datePaid"));
        return paymentRepo.findAll(sort).stream()
                .map(PaymentRecords::DTOmapper)
                .toList();
    }

    public PagedModel<EntityModel<PaymentRecordDTO>> getAllTimeRecordsByDate(String condition, int page){
        Sort sort = condition.equalsIgnoreCase("desc") ?
                Sort.by(Sort.Order.desc("datePaid")) :
                Sort.by(Sort.Order.asc("datePaid"));
        Page<PaymentRecordDTO> recordsPage = paymentRepo.findAll(PageRequest.of(page,15,sort)).map(PaymentRecords::DTOmapper);
        return pagedResourcesAssembler.toModel(recordsPage);
    }

    public List<PaymentRecordDTO> getAllStudentPaymentRecords(int studentId){
        return paymentRepo.getAllStudentPaymentRecord(studentId).stream()
                            .map(PaymentRecords::DTOmapper)
                            .toList();
    }
    
    public List<PaymentRecordDTO> getAllStudentPaymentRecordsByName(String studentName){
        try{
            Student student = studentRepo.findByStudentDisplayIDOrName(studentName).orElse(null);
            assert student != null;
            return paymentRepo.getAllStudentPaymentRecord(student.getStudentId()).stream()
                                .map(PaymentRecords::DTOmapper)
                                .toList();
        }catch(NullPointerException npe){
            return null;
        }
    }

    public PagedModel<EntityModel<PaymentRecordDTO>> testingPageable(String condition, int page, int size){
        Sort sort = condition.equalsIgnoreCase("desc") ?
                    Sort.by(Sort.Order.desc("datePaid")) :
                    Sort.by(Sort.Order.asc("datePaid"));
        Page<PaymentRecords> recordsPage = paymentRepo.getRecordsPage(PageRequest.of(page, size, sort));

        return pagedResourcesAssembler.toModel(recordsPage.map(PaymentRecords::DTOmapper));
    }
    
}
