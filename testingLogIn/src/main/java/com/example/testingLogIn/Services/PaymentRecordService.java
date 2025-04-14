package com.example.testingLogIn.Services;

import com.example.testingLogIn.AssociativeModels.GradeLevelRequiredFees;
import com.example.testingLogIn.AssociativeModels.StudentFeesList;
import com.example.testingLogIn.CustomObjects.EnrollmentPaymentView;
import com.example.testingLogIn.CustomObjects.FeesAndBalance;
import com.example.testingLogIn.CustomObjects.StudentPaymentForm;
import com.example.testingLogIn.CustomObjects.StudentTotalDiscount;
import com.example.testingLogIn.ModelDTO.PaymentRecordDTO;
import com.example.testingLogIn.ModelDTO.PaymentTransactionDTO;
import com.example.testingLogIn.Models.*;
import com.example.testingLogIn.PagedResponse.PagedResponse;
import com.example.testingLogIn.PagedResponse.PaymentTransactionDTOPage;
import com.example.testingLogIn.Repositories.*;
import com.example.testingLogIn.WebsiteSecurityConfiguration.CustomUserDetailsService;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.stereotype.Service;

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
    public PaymentRecordService(PaymentsRecordRepo paymentRepo, StudentRepo studentRepo, sySemesterRepo SYSemRepo,
                                CustomUserDetailsService userService, GradeLevelRequiredFeeRepo gradeReqFee,
                                StudentFeesListRepo studFeesRepo) {
        this.paymentRepo = paymentRepo;
        this.studentRepo = studentRepo;
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
        transaction.setTotalAmount(amount);
        transaction.setNotVoided(true);
        PaymentTransaction tran = transactionRepo.save(transaction);
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
            List<GradeLevelRequiredFees> requiredFees = gradeReqFee.findByGradeLevel(gradeLevelId);
            for (GradeLevelRequiredFees reqFee : requiredFees) {
                double paidAmount = Optional.ofNullable(paymentRepo.totalPaidForSpecificFee(studentId, reqFee.getRequiredFee().getId(), sem.getSySemNumber())).orElse(0.0d);
                double initialAmount = reqFee.getRequiredFee().getRequiredAmount();
                double discountedAmount = NonModelServices.adjustDecimal(initialAmount - ((initialAmount*std.getTotalPercentageDiscount())+std.getTotalFixedDiscount()));
                double remainingBalance = discountedAmount - paidAmount;
                if(remainingBalance>0){
                    studentPaymentForm.getFeesAndBalance().add(new FeesAndBalance(reqFee.getRequiredFee(),remainingBalance,paidAmount,discountedAmount));
                    totalBalance+=remainingBalance;}
            }
        }else{//For all time debt
            for(StudentFeesList sfl : studFeesRepo.findBySem(studentId,null)) {
                if (!feesBalance.containsValue(sfl.getFee())) {
                    double totalPaidAmount = Optional.ofNullable(paymentRepo.totalPaidForSpecificFee(studentId, sfl.getFee().getId(), null)).orElse(0.0);//(studentId,feeId,null)
                    double totalFeeBalance = studFeesRepo.totalPerFeesByStudent(studentId, sfl.getFee().getId());
                    double remainingBalance = NonModelServices.adjustDecimal(totalFeeBalance - totalPaidAmount);
                    if (remainingBalance > 0) {
                        totalBalance += remainingBalance;
                        studentPaymentForm.getFeesAndBalance().add(new FeesAndBalance(sfl.getFee(),remainingBalance,totalPaidAmount,totalFeeBalance));
                    }
                }
            }
        }
        studentPaymentForm.setTotalFee(totalBalance);
        return studentPaymentForm;
    }

    public PagedResponse getTransactions(int pageNo, int pageSize,String type,String search){
        Pageable pageable = PageRequest.of(pageNo-1,pageSize);
        Page<?> transactionPage = null;

        if(type.equalsIgnoreCase("Transaction"))
            transactionPage = transactionRepo.getTransactionsIsVoided(search,true,pageable).map(PaymentTransaction::DTOmapper);
        else if(type.equalsIgnoreCase("Voided"))
            transactionPage = transactionRepo.getTransactionsIsVoided(search,false,pageable).map(PaymentTransaction::DTOmapper);
        else
            transactionPage = paymentRepo.getRecordsByFee(Integer.parseInt(type),pageable).map(PaymentRecords::DTOmapper);

        return PagedResponse.builder()
                .content(transactionPage.getContent())
                .totalPages(transactionPage.getTotalPages())
                .totalElements(transactionPage.getTotalElements())
                .pageSize(transactionPage.getSize())
                .pageNo(transactionPage.getNumber())
                .isLast(transactionPage.isLast())
                .build();
    }

    public PaymentTransactionDTO getTransactionById(String referenceId){
        return transactionRepo.findById(referenceId).map(PaymentTransaction::DTOmapper).orElse(null);
    }

    @CacheEvict(value = "enrollmentPage",allEntries = true)
    public boolean voidThePayment(String transactionId){
        PaymentTransaction transaction = transactionRepo.findById(transactionId).orElseThrow(NullPointerException::new);
        Student stud = transaction.getStudent();
        transaction.setNotVoided(false);
        stud.setStudentBalance(stud.getStudentBalance()+ transaction.getTotalAmount());
        studentRepo.save(stud);
        transactionRepo.save(transaction);
        return true;
    }

    private PaymentTransaction generateTransaction(){
        int transactionCount = Optional.ofNullable(transactionRepo.countTotalTransactions()).orElse(0);
        LocalDate currentDate = LocalDate.now();
        String transactionId = String.valueOf(   currentDate.getYear()+""+
                                                currentDate.getMonthValue()+
                                                currentDate.getDayOfWeek().toString().charAt(0)+
                                                transactionCount);
        return PaymentTransaction.builder()
                .transactionId(transactionId)
                .staff(userService.getCurrentlyLoggedInUser())
                .dateReceived(currentDate)
                .build();
    }
    
}
