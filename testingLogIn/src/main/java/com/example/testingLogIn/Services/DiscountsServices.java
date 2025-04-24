package com.example.testingLogIn.Services;

import com.example.testingLogIn.AssociativeModels.StudentFeesList;
import com.example.testingLogIn.CustomObjects.StudentDiscountHandler;
import com.example.testingLogIn.CustomObjects.StudentTotalDiscount;
import com.example.testingLogIn.ModelDTO.StudentDiscountDTO;
import com.example.testingLogIn.Models.Discount;
import com.example.testingLogIn.Models.SchoolYearSemester;
import com.example.testingLogIn.Models.Student;
import com.example.testingLogIn.AssociativeModels.StudentDiscount;
import com.example.testingLogIn.CustomObjects.PagedResponse;
import com.example.testingLogIn.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class DiscountsServices {

    private final DiscountRepo discRepo;
    private final StudentDiscountRepo studDiscRepo;
    private final StudentRepo studRepo;
    private final StudentFeesListRepo sfl;
    private final sySemesterServices sem;
    private final PaymentsRecordRepo paymentRepo;

    public DiscountsServices(DiscountRepo discRepo, StudentDiscountRepo studDiscRepo, StudentRepo studRepo, StudentFeesListRepo sfl, sySemesterServices sem, PaymentsRecordRepo paymentRepo) {
        this.discRepo = discRepo;
        this.studDiscRepo = studDiscRepo;
        this.studRepo = studRepo;
        this.sfl = sfl;
        this.sem = sem;
        this.paymentRepo = paymentRepo;
    }

    public List<Discount> getDiscountsList(boolean isNotDeleted){
        if(isNotDeleted)
            return discRepo.findByIsNotDeletedTrue().stream().peek(discount -> discount.setPercentageDiscount(100*discount.getPercentageDiscount())).toList();
        return discRepo.findByIsNotDeletedFalse();
    }

    public boolean addDiscount(Discount discount){
        if(!discRepo.findDiscountByName(discount.getDiscountName().trim()).isEmpty())
            return false;
        discount.setNotDeleted(true);
        discRepo.save(discount);

        return true;
    }

    public void deleteDiscount(int discountId){
        Discount discount = discRepo.findById(discountId).orElseThrow(NullPointerException::new);
        discount.setNotDeleted(false);
        discRepo.save(discount);

        CompletableFuture.runAsync(()->{
            SchoolYearSemester actvSem = sem.getCurrentActive();
            if(actvSem != null){
            int currentSem = actvSem.getSySemNumber();
            for(Student student : studDiscRepo.findStudentByDiscount(discountId, currentSem)){
                updateStudentFees(student);}}
        });
    }

    public void addStudentDiscount(int discountId, List<Integer> studentIds){
        Discount discount = discRepo.findById(discountId).orElse(null);
        assert discount != null;
        studentIds.forEach(studentId -> {
            StudentDiscount studentDiscount = studDiscRepo.findStudentDiscountRecord(studentId,discountId).orElse(null);
            Student student = studRepo.findById(studentId).orElse(null);
            assert student != null;
            if(studentDiscount == null){
                studentDiscount = StudentDiscount.builder()
                        .discount(discount)
                        .student(student)
                        .isNotDeleted(true)
                        .build();
            }
            studentDiscount.setNotDeleted(true);
            studDiscRepo.save(studentDiscount);
            updateStudentFees(student);
        });
    }

    public boolean addStudentDiscounts(Integer studentId, List<Integer> discounts){
        Student student = studRepo.findById(studentId).orElseThrow(NullPointerException::new);
        List<StudentDiscount> studentDiscounts = new ArrayList<>();

        studDiscRepo.findByStudent(student.getStudentId())
            .forEach(studentDiscount -> {
                studentDiscount.setNotDeleted(true);
                if(!discounts.contains(studentDiscount.getDiscount().getDiscountId()))
                    studentDiscount.setNotDeleted(false);
                discounts.remove(Integer.valueOf(studentDiscount.getDiscount().getDiscountId()));
                studentDiscounts.add(studentDiscount);
            });
        discounts.forEach(discId ->{
            discRepo.findById(discId).
                    ifPresent(discount -> studentDiscounts.add(new StudentDiscount(student, discount)));}
        );

        if(!studentDiscounts.isEmpty()){
            studDiscRepo.saveAll(studentDiscounts);
            updateStudentFees(student);}
        return true;
    }

    public StudentTotalDiscount getStudentTotalDiscount(int studentId){
        return studDiscRepo.getStudentTotalDiscount(studentId).orElse(new StudentTotalDiscount(null,null));
    }

    public PagedResponse getDiscountRecords(int pageNo, int pageSize, Integer discountId,String search, boolean didAvailed){
        Pageable pageable = PageRequest.of(pageNo-1,pageSize);
        search = NonModelServices.forLikeOperator(search);
        Page<StudentDiscountHandler> studDiscPage = studDiscRepo.findRecords(discountId, didAvailed,search, pageable);
        List<StudentDiscountDTO> content = studDiscPage.getContent().stream()
                                                .map(StudentDiscountHandler::DTOmapper).toList();
        return PagedResponse.builder()
                .content(content)
                .pageNo(studDiscPage.getNumber())
                .pageSize(studDiscPage.getSize())
                .totalElements(studDiscPage.getNumberOfElements())
                .totalPages(studDiscPage.getTotalPages())
                .isLast(studDiscPage.isLast())
                .build();
    }

    public void removeStudentsDiscount(List<Integer> connectionIds){
        connectionIds.forEach(conId ->{
            studDiscRepo.findById(conId).ifPresent(studentDiscount -> {
                studentDiscount.setNotDeleted(false);
                studDiscRepo.save(studentDiscount);
                updateStudentFees(studentDiscount.getStudent());
            });
        });
    }

    public void removeStudentDiscount(int discId){
        StudentDiscount studentDiscount = studDiscRepo.findById(discId).orElseThrow(NullPointerException::new);
        studentDiscount.setNotDeleted(false);
        studDiscRepo.save(studentDiscount);
        updateStudentFees(studentDiscount.getStudent());
    }

    public void removeStudentDiscounts(int studentId){
        List<Integer> connectionIds = studDiscRepo.findByStudentNotDeleted(studentId).stream().map(StudentDiscount::getConnectionId).toList();
        removeStudentsDiscount(connectionIds);
    }

    @CacheEvict(value = {"enrollmentPage","studPaymentForm"},allEntries = true)
    private void updateStudentFees(Student student) {
        int currentSemId = Optional.ofNullable(sem.getCurrentActive()).map(SchoolYearSemester::getSySemNumber).orElse(0);
        StudentTotalDiscount studDiscount = studDiscRepo.getStudentTotalDiscount(student.getStudentId()).orElse(null);
        if (studDiscount != null && currentSemId>0) {
            CompletableFuture.runAsync(() -> {
                int studentId = student.getStudentId();
                double percentDisc = studDiscount.getTotalPercentageDiscount();
                double fixedDisc = studDiscount.getTotalFixedDiscount();
                double toAddStudentBalance = 0;
                List<StudentFeesList> studentFeesLists = sfl.getFeesBySemAndStudent(studentId, currentSemId);
                if (!studentFeesLists.isEmpty()) {
                    for (StudentFeesList studFee : studentFeesLists) {
                        double feeAmount = studFee.getFee().getRequiredAmount();
                        double newBalance = feeAmount - NonModelServices.adjustDecimal((feeAmount * percentDisc) + fixedDisc);
                        toAddStudentBalance += newBalance - studFee.getAmount();
                        studFee.setAmount(newBalance);
                        sfl.save(studFee);
                    }
                    //Option 1 : Will deduct the difference between new fee balance and initial to be paid amount to student's current balance
//                    student.setScholar(!studDiscRepo.findByStudentNotDeleted(studentId).isEmpty());
//                    student.setStudentBalance(student.getStudentBalance() + toAddStudentBalance);
//                    studRepo.save(student);

                    //Option 2 : Will recalculate all of the balance from all fees then the total balance will become the new student balance
                    double newBalance = 0;
                    for(StudentFeesList paid : sfl.findUniqueFees(studentId)){
                        Double amountPaid = paymentRepo.totalPaidForSpecificFee(studentId,paid.getFee().getId(),null).orElse(0d);
                        newBalance += NonModelServices.zeroIfLess(Optional.ofNullable(sfl.totalPerFeesByStudent(studentId, paid.getFee().getId())).orElse(0d) - amountPaid);

                    }
                    student.setScholar(!studDiscRepo.findByStudentNotDeleted(studentId).isEmpty());
                    student.setStudentBalance(NonModelServices.adjustDecimal(newBalance));
                    studRepo.save(student);
                }
            });
        }
    }
}
