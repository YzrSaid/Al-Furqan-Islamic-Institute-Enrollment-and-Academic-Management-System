package com.example.testingLogIn.Services;

import com.example.testingLogIn.AssociativeModels.GradeLevelRequiredFees;
import com.example.testingLogIn.AssociativeModels.StudentFeesList;
import com.example.testingLogIn.CustomObjects.FeesAndBalance;
import com.example.testingLogIn.CustomObjects.StudentPaymentForm;
import com.example.testingLogIn.CustomObjects.StudentTotalDiscount;
import com.example.testingLogIn.ModelDTO.PaymentRecordDTO;
import com.example.testingLogIn.ModelDTO.PaymentTransactionDTO;
import com.example.testingLogIn.Models.*;
import com.example.testingLogIn.CustomObjects.PagedResponse;
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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    @CacheEvict(value = {"enrollmentPage"//,"studPaymentForm"
    },allEntries = true)
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
                    double paidAmount = paymentRepo.totalPaidForSpecificFee(studentId, reqFee.getRequiredFee().getId(), sem.getSySemNumber()).orElse(0.0);

                    double initialAmount = reqFee.getRequiredFee().getRequiredAmount();//initial amount deducted by the discount
                    double discountedAmount = initialAmount-((initialAmount * std.getTotalPercentageDiscount())+std.getTotalFixedDiscount());
                    double totalFeeBalance = discountedAmount - paidAmount;
                    if (totalFeeBalance > 0)
                        toSortByBalance.add(MapperObject.builder()
                                .requiredFees(reqFee.getRequiredFee())
                                .totalBalance(NonModelServices.adjustDecimal(totalFeeBalance))
                                .build());
                }
            }
        }else {//all time fees
            for(StudentFeesList paid : studFeesRepo.findUniqueFees(studentId)){
                if(feesId.contains(paid.getFee().getId())) {
                    Double amountPaid = paymentRepo.totalPaidForSpecificFee(studentId,paid.getFee().getId(),null).orElse(0d);
                    double remainingBalance = Optional.ofNullable(studFeesRepo.totalPerFeesByStudent(studentId, paid.getFee().getId())).orElse(0d) - amountPaid;
                    if (remainingBalance > 0)
                        toSortByBalance.add(MapperObject.builder()
                                .requiredFees(paid.getFee())
                                .totalBalance(NonModelServices.adjustDecimal(remainingBalance))
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
                    .amount(NonModelServices.adjustDecimal(toAllocate))
                    .build();
            paymentRepo.save(particular);
            tran.getParticulars().add(particular);
        });
        student.setStudentBalance(student.getStudentBalance()-amount);
        studentRepo.save(student);
        return tran.DTOmapper();
    }

//    @Cacheable(
//            value = "studPaymentForm",
//            key = "#studentId + #pageNo + #pageSize")
    public Object getStudentPaymentForm(int studentId, boolean forBreakDown, int pageNo, int pageSize, boolean forPayment){
        Student student = studentRepo.findById(studentId).orElse(null);
        assert student != null;
        StudentPaymentForm studentPaymentForm = StudentPaymentForm.preBuilt(student.DTOmapper());
        List<StudentFeesList> pagedFees = studFeesRepo.findBySem(studentId,null);

        double totalRemaining=0;
        for(StudentFeesList sfl : pagedFees) {
            double totalPaidAmount = paymentRepo.totalPaidForSpecificFee(studentId, sfl.getFee().getId(), null).orElse(0.0);//(studentId,feeId,null)
            double totalFeeBalance = studFeesRepo.totalPerFeesByStudent(studentId, sfl.getFee().getId());
            double remainingBalance = NonModelServices.adjustDecimal(totalFeeBalance - totalPaidAmount);
            if (remainingBalance > 0 && !forBreakDown) {
                totalRemaining+=remainingBalance;
                studentPaymentForm.getFeesAndBalance().add(new FeesAndBalance(sfl.getFee(),remainingBalance,NonModelServices.adjustDecimal(totalPaidAmount),totalFeeBalance));
            }else if(forBreakDown && totalFeeBalance > 0 && remainingBalance == 0){
                totalRemaining+=remainingBalance;
                studentPaymentForm.getFeesAndBalance().add(new FeesAndBalance(sfl.getFee(),remainingBalance,NonModelServices.adjustDecimal(totalPaidAmount),totalFeeBalance));
            }
        }
        studentPaymentForm.setTotalFee(totalRemaining);

        if(forPayment)
            return studentPaymentForm;

        double totalPage = Math.ceil((float) studentPaymentForm.getFeesAndBalance().size() /pageSize);
        int totalElements = studentPaymentForm.getFeesAndBalance().size();
        pageNo = (int) Math.min(pageNo, totalPage);
        int max = (pageNo*pageSize)-1;
        int min = (pageNo*pageSize)-pageSize;
        ArrayList<FeesAndBalance> toReturn = new ArrayList<>();
        while(min<=max){
            try {
                toReturn.add(studentPaymentForm.getFeesAndBalance().get(min));
                min++;
            }catch (IndexOutOfBoundsException ioobe){
                break;
            }
        }
        return PagedResponse.builder()
                .content(toReturn)
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalElements(totalElements)
                .totalPages((int) totalPage)
                .build();
    }

    public StudentPaymentForm enrollmentPaymentForm(int studentId, int levelId){
        Student student = studentRepo.findById(studentId).orElse(null);
        SchoolYearSemester sem = SYSemRepo.findCurrentActive();
        assert student != null;
        StudentPaymentForm studentPaymentForm = StudentPaymentForm.preBuilt(student.DTOmapper());

        double totalBalance = 0;
        StudentTotalDiscount std = discService.getStudentTotalDiscount(studentId);
        List<GradeLevelRequiredFees> requiredFees = gradeReqFee.findByGradeLevel(levelId);
        for (GradeLevelRequiredFees reqFee : requiredFees) {
            double paidAmount = paymentRepo.totalPaidForSpecificFee(studentId, reqFee.getRequiredFee().getId(), sem.getSySemNumber()).orElse(0d);
            double initialAmount = reqFee.getRequiredFee().getRequiredAmount();
            double discountedAmount = initialAmount - ((initialAmount*std.getTotalPercentageDiscount())+std.getTotalFixedDiscount());
            double remainingBalance = NonModelServices.adjustDecimal(discountedAmount - paidAmount);
            if(remainingBalance>0){
                studentPaymentForm.getFeesAndBalance().add(new FeesAndBalance(reqFee.getRequiredFee(),remainingBalance,paidAmount,discountedAmount));
                totalBalance+=remainingBalance;}
        }
        studentPaymentForm.setTotalFee(totalBalance);
        return studentPaymentForm;
    }

    public PagedResponse getTransactions(int pageNo, int pageSize,String type,String search){
        Pageable pageable = PageRequest.of(pageNo-1,pageSize , Sort.by(Sort.Order.desc("pt.dateReceived")));
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

    @CacheEvict(value = {"enrollmentPage"//,"studPaymentForm"
    },allEntries = true)
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
