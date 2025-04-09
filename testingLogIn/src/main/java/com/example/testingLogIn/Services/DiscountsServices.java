package com.example.testingLogIn.Services;

import com.example.testingLogIn.AssociativeModels.StudentFeesList;
import com.example.testingLogIn.CustomObjects.StudentDiscountHandler;
import com.example.testingLogIn.CustomObjects.StudentTotalDiscount;
import com.example.testingLogIn.ModelDTO.StudentDiscountDTO;
import com.example.testingLogIn.Models.Discount;
import com.example.testingLogIn.Models.Student;
import com.example.testingLogIn.AssociativeModels.StudentDiscount;
import com.example.testingLogIn.PagedResponse.PagedResponse;
import com.example.testingLogIn.Repositories.DiscountRepo;
import com.example.testingLogIn.Repositories.StudentDiscountRepo;
import com.example.testingLogIn.Repositories.StudentFeesListRepo;
import com.example.testingLogIn.Repositories.StudentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class DiscountsServices {

    private final DiscountRepo discRepo;
    private final StudentDiscountRepo studDiscRepo;
    private final StudentRepo studRepo;
    private final StudentFeesListRepo sfl;
    private final sySemesterServices sem;

    @Autowired
    public DiscountsServices(DiscountRepo discRepo, StudentDiscountRepo studDiscRepo, StudentRepo studRepo, StudentFeesListRepo sfl, sySemesterServices sem) {
        this.discRepo = discRepo;
        this.studDiscRepo = studDiscRepo;
        this.studRepo = studRepo;
        this.sfl = sfl;
        this.sem = sem;
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
    }

    public boolean addStudentDiscount(int discountId, List<Integer> studentIds){
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
        return true;
    }

    //para mag update ng student info
    public boolean addStudentDiscounts(Integer studentId, List<Integer> discounts){
        Student student = studRepo.findById(studentId).orElseThrow(NullPointerException::new);
        List<StudentDiscount> studentDiscounts = new ArrayList<>();

        studDiscRepo.findByStudent(student.getStudentId())
            .forEach(studentDiscount -> {
                studentDiscount.setNotDeleted(true);
                if(!discounts.contains(studentDiscount.getDiscount().getDiscountId()))
                    studentDiscount.setNotDeleted(true);
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
        StudentTotalDiscount std= studDiscRepo.getStudentTotalDiscount(studentId).orElse(new StudentTotalDiscount(null,null));
        return std;
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

    private void updateStudentFees(Student student) {
        int currentSemId = sem.getCurrentActive().getSySemNumber();
        StudentTotalDiscount studDiscount = studDiscRepo.getStudentTotalDiscount(student.getStudentId()).orElse(null);
        if (studDiscount != null) {
            CompletableFuture.runAsync(() -> {
                double percentDisc = studDiscount.getTotalPercentageDiscount();
                double fixedDisc = studDiscount.getTotalFixedDiscount();
                double toAddStudentBalance = 0;
                List<StudentFeesList> studentFeesLists = sfl.getFeesBySemAndStudent(student.getStudentId(), currentSemId);
                if (!studentFeesLists.isEmpty()) {
                    for (StudentFeesList studFee : studentFeesLists) {
                        double feeAmount = studFee.getFee().getRequiredAmount();
                        double newBalance = feeAmount - NonModelServices.adjustDecimal((feeAmount * percentDisc) + fixedDisc);
                        toAddStudentBalance += studFee.getAmount() - newBalance;
                        studFee.setAmount(newBalance);
                        sfl.save(studFee);
                    }
                    student.setScholar(!studDiscRepo.findByStudentNotDeleted(student.getStudentId()).isEmpty());
                    student.setStudentBalance(student.getStudentBalance() + toAddStudentBalance);
                    studRepo.save(student);
                }
            });
        }
    }
}
