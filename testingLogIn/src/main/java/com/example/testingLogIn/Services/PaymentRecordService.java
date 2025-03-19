package com.example.testingLogIn.Services;

import com.example.testingLogIn.CustomObjects.FeesAndBalance;
import com.example.testingLogIn.CustomObjects.StudentPaymentForm;
import com.example.testingLogIn.CustomObjects.StudentTotalDiscount;
import com.example.testingLogIn.ModelDTO.PaymentRecordDTO;
import com.example.testingLogIn.CustomObjects.TotalPaid;
import com.example.testingLogIn.ModelDTO.PaymentTransactionDTO;
import com.example.testingLogIn.Models.*;
import com.example.testingLogIn.Repositories.*;
import com.example.testingLogIn.WebsiteSecurityConfiguration.CustomUserDetailsService;

import java.time.DayOfWeek;
import java.time.LocalDate;
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
    @Autowired
    private PaymentTransactionRepo transactionRepo;

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
    //NEW WAY OF PAYING
    public PaymentTransactionDTO addPaymentAutoAllocate(int studentId, Integer gradeLevelId, double amount, List<Integer> feesId){
        PaymentTransaction transaction = generateTransaction();
        Student student = studentRepo.findById(studentId).orElse(null);
        SchoolYearSemester sem = SYSemRepo.findCurrentActive();
        assert student!=null;
        transaction.setStudent(student);
        transaction.setSYSem(sem);
        transactionRepo.save(transaction);
        PaymentTransaction tran = transactionRepo.findById(transaction.getTransactionId()).orElse(null);
        assert tran != null;
        tran.setParticulars(new ArrayList<>());

        List<MapperObject> toSortByBalance = new ArrayList<>();
        setTheAmount(amount);
        StudentTotalDiscount std = discService.getStudentTotalDiscount(studentId);

        if(gradeLevelId != null) {//during enrollment
            for (GradeLevelRequiredFees reqFee : gradeReqFee.findByGradeLevel(gradeLevelId)) {
                if(feesId.contains(reqFee.getRequiredFee().getId())) {
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

            PaymentRecords particular = PaymentRecords.builder()
                    .transaction(tran)
                    .requiredPayment(fee.getRequiredFees())
                    .amount(toAllocate)
                    .build();
            paymentRepo.save(particular);
            tran.getParticulars().add(particular);
        });
        student.setStudentBalance(student.getStudentBalance()-amount);
        studentRepo.save(student);
        return tran.DTOmapper();
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

    private PaymentTransaction generateTransaction(){
        int transactionCount = Optional.ofNullable(transactionRepo.countTotalTransactions()).orElse(0);
        LocalDate currentDate = LocalDate.now();
        String transactionId = String.valueOf(   currentDate.getYear()+""+
                                                currentDate.getMonthValue()+""+
                                                currentDate.getDayOfWeek().toString().charAt(0)+
                                                transactionCount);
        return PaymentTransaction.builder()
                .transactionId(transactionId)
                .staff(userService.getCurrentlyLoggedInUser())
                .dateReceived(currentDate)
                .build();
    }
    
}
