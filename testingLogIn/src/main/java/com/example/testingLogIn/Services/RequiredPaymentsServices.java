package com.example.testingLogIn.Services;

import com.example.testingLogIn.AssociativeModels.StudentFeesList;
import com.example.testingLogIn.CustomObjects.StudentTotalDiscount;
import com.example.testingLogIn.ModelDTO.GradeLevelToRequiredPaymentDTO;
import com.example.testingLogIn.ModelDTO.RequiredPaymentsDTO;
import com.example.testingLogIn.Models.GradeLevel;
import com.example.testingLogIn.AssociativeModels.GradeLevelRequiredFees;
import com.example.testingLogIn.Models.RequiredFees;
import com.example.testingLogIn.Models.Student;
import com.example.testingLogIn.Repositories.EnrollmentRepo;
import com.example.testingLogIn.Repositories.GradeLevelRequiredFeeRepo;
import com.example.testingLogIn.Repositories.RequiredPaymentsRepo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.example.testingLogIn.Repositories.StudentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author magno
 */
@Service
public class RequiredPaymentsServices {
    private final RequiredPaymentsRepo reqPaymentsRepo;
    private final GradeLevelServices gradeLevelService;
    private final GradeLevelRequiredFeeRepo reqFeeGradelvlRepo;
    private final EnrollmentRepo enrollmentRepo;
    private final sySemesterServices semServices;
    private final DiscountsServices discService;
    private final StudentRepo studentRepo;
    private final StudentFeesListService sfl;
    private final DistributableServices distributableServices;

    @Autowired
    public RequiredPaymentsServices(RequiredPaymentsRepo reqPaymentsRepo, GradeLevelServices gradeLevelService, GradeLevelRequiredFeeRepo reqFeeGradelvlRepo, EnrollmentRepo enrollmentRepo, sySemesterServices semServices, DiscountsServices discService, StudentRepo studentRepo, StudentFeesListService sfl, DistributableServices distributableServices) {
        this.reqPaymentsRepo = reqPaymentsRepo;
        this.gradeLevelService = gradeLevelService;
        this.reqFeeGradelvlRepo = reqFeeGradelvlRepo;
        this.enrollmentRepo = enrollmentRepo;
        this.semServices = semServices;
        this.discService = discService;
        this.studentRepo = studentRepo;
        this.sfl = sfl;
        this.distributableServices = distributableServices;
    }

    public RequiredFees getRequiredPaymentById(int reqPaymentId){
        return reqPaymentsRepo.findById(reqPaymentId).orElse(null);
    }
    
    public int addNewPayments(RequiredPaymentsDTO paymentsDTO){
        boolean doesNameExist = reqPaymentsRepo.findAll().stream()
                                                .filter(payment -> payment.isNotDeleted() && 
                                                                    payment.getName().equalsIgnoreCase(paymentsDTO.getName()))
                                                .toList().isEmpty();
        if(!doesNameExist)
            return 2;
        else{
            RequiredFees reqFee = RequiredFees.builder()
                                            .name(paymentsDTO.getName())
                                            .isNotDeleted(true)
                                            .requiredAmount(paymentsDTO.getRequiredAmount())
                                            .build();
            RequiredFees newFee = reqPaymentsRepo.save(reqFee);
            List<GradeLevel> gradeLevels = gradeLevelService.getAllGradeLevels().stream()
                                                        .filter(gradelvl -> paymentsDTO.getGradeLevelNames().contains(gradelvl.getLevelName()) && gradelvl.isNotDeleted())
                                                        .toList();

            gradeLevels.forEach(gradelvl -> reqFeeGradelvlRepo.save(GradeLevelRequiredFees.build(gradelvl,newFee)));
            if (paymentsDTO.isWillApplyNow())
                runMe(() -> {
                    List<Student> studentList = new ArrayList<>();
                    gradeLevels.forEach(gradelvl -> studentList.addAll(enrollmentRepo.getCurrentlyEnrolledToGrade(gradelvl.getLevelId(), semServices.getCurrentActive().getSySemNumber())));

                    studentList.forEach(student -> {
                        StudentTotalDiscount std = discService.getStudentTotalDiscount(student.getStudentId());
                        double addToBalance = newFee.getRequiredAmount() - Math.ceil(((newFee.getRequiredAmount() * std.getTotalPercentageDiscount()) + std.getTotalFixedDiscount()));
                        student.setStudentBalance(student.getStudentBalance() + addToBalance);
                        studentRepo.save(student);
                        sfl.addFeeRecord(student, newFee, semServices.getCurrentActive(), addToBalance);
                    });
                });

            if(paymentsDTO.isDistributable()){
                if(!distributableServices.addNewDistributable(paymentsDTO.getName(),gradeLevels,null))
                    return 1;
            }
        }
        return 0;
    }


    public List<RequiredPaymentsDTO> getAllPayments(){
        List<RequiredFees> paymentsList = reqPaymentsRepo.findAll().stream()
                                .filter(RequiredFees::isNotDeleted)
                                .toList();
        List<RequiredPaymentsDTO> uniquePayments = new ArrayList<>();
        
        for(RequiredFees payment : paymentsList){
                RequiredPaymentsDTO paymentDTO = RequiredPaymentsDTO.builder()
                                                        .id(payment.getId())
                                                        .name(payment.getName())
                                                        .requiredAmount(payment.getRequiredAmount())
                                                        .isDeleted(true)
                                                        .gradeLevelNames(new ArrayList<String>())
                                                        .build();
                reqFeeGradelvlRepo.findByRequiredFee(payment.getId()).stream()
                        .filter(GradeLevelRequiredFees::isNotDeleted)
                        .forEach(gradelvl -> {
                            paymentDTO.getGradeLevelNames().add(gradelvl.getGradeLevel().getLevelName());
                        });
                uniquePayments.add(paymentDTO);
        }
        
        return uniquePayments;
    }
    
    public List<GradeLevelToRequiredPaymentDTO> getPaymentsByGradeLevel(int gradeLevelId){
        return reqFeeGradelvlRepo.findByGradeLevel(gradeLevelId).stream()
                        .filter(GradeLevelRequiredFees::isNotDeleted)
                        .map(GradeLevelRequiredFees::DTOmapper)
                        .toList();
    }

    public boolean updatePayment(int feeId, RequiredPaymentsDTO updated){
        RequiredFees toUpdate = reqPaymentsRepo.findById(feeId).orElse(null);
        assert toUpdate != null;
        toUpdate.setName(updated.getName());
        toUpdate.setRequiredAmount(updated.getRequiredAmount());
        reqPaymentsRepo.save(toUpdate);
        int currentSemId = semServices.getCurrentActive().getSySemNumber();

        List<GradeLevelRequiredFees> affectedRows = reqFeeGradelvlRepo.findByRequiredFee(feeId);
        
        for(GradeLevelRequiredFees payment : affectedRows){
            String toRemoveGrade = payment.getGradeLevel().getLevelName();
            int levelId = payment.getGradeLevel().getLevelId();
            List<Student> studentList = enrollmentRepo.getCurrentlyEnrolledToGrade(levelId,currentSemId);
            if(!updated.getGradeLevelNames().contains(toRemoveGrade))//{
                payment.setNotDeleted(false);
//                if(updated.isWillApplyNow())
//                    runMe(()->studentList.forEach(student -> {
//                        StudentFeesList studFee = sfl.studentFeesList(student.getStudentId(),levelId,currentSemId);
//                        if(studFee != null){
//                            student.setStudentBalance(student.getStudentBalance()- studFee.getAmount());
//                            studFee.setAmount(0.0d);
//                            sfl.updateFeeRecord(studFee);
//                            studentRepo.save(student);
//                        }}));
//            }else{
//                if(updated.isWillApplyNow())
//                    runMe(()->studentList.forEach(student -> {
//                        StudentFeesList studFee = sfl.studentFeesList(student.getStudentId(),levelId,currentSemId);
//                        if(studFee != null){
//                            StudentTotalDiscount std = discService.getStudentTotalDiscount(student.getStudentId());
//                            double newBalToPay = updated.getRequiredAmount() - Math.ceil((updated.getRequiredAmount()*std.getTotalPercentageDiscount())+ std.getTotalFixedDiscount());
//                            student.setStudentBalance(student.getStudentBalance() +(newBalToPay - studFee.getAmount()));
//                            studFee.setAmount(newBalToPay);
//                            sfl.updateFeeRecord(studFee);
//                            studentRepo.save(student);
//                        }}));
//            }
            if(updated.isWillApplyNow()){
                runMe(()->studentList.forEach(student -> {
                    StudentFeesList studFee = sfl.studentFeesList(student.getStudentId(),levelId,currentSemId);
                    if(studFee != null){
                        double newBalToPay;
                        if(payment.isNotDeleted()){
                            StudentTotalDiscount std = discService.getStudentTotalDiscount(student.getStudentId());
                            newBalToPay = updated.getRequiredAmount() - Math.ceil((updated.getRequiredAmount()*std.getTotalPercentageDiscount())+ std.getTotalFixedDiscount());
                            student.setStudentBalance(student.getStudentBalance() +(newBalToPay - studFee.getAmount()));
                        }else{
                            student.setStudentBalance(student.getStudentBalance()- studFee.getAmount());
                            newBalToPay= 0.0d;
                        }
                        studFee.setAmount(newBalToPay);
                        sfl.updateFeeRecord(studFee);
                        studentRepo.save(student);
                    }}));
            }
            runMe(()->reqFeeGradelvlRepo.save(payment));
            updated.getGradeLevelNames().remove(toRemoveGrade);
        }
        
        if(!updated.getGradeLevelNames().isEmpty()){
            for(String gradeLevelName : updated.getGradeLevelNames()){
                GradeLevel gradeLevel = gradeLevelService.getByName(gradeLevelName);
                reqFeeGradelvlRepo.save(GradeLevelRequiredFees.build(gradeLevel,toUpdate));

                //if (updated.isWillApplyNow()) will implement tomorrow
                    runMe(() -> {
                        List<Student> studentList =     enrollmentRepo.getCurrentlyEnrolledToGrade(gradeLevel.getLevelId(),currentSemId);
                        studentList.forEach(student -> {
                            StudentTotalDiscount std = discService.getStudentTotalDiscount(student.getStudentId());
                            double addToBalance = toUpdate.getRequiredAmount() - Math.ceil(((toUpdate.getRequiredAmount() * std.getTotalPercentageDiscount()) + std.getTotalFixedDiscount()));
                            student.setStudentBalance(student.getStudentBalance() + addToBalance);
                            studentRepo.save(student);
                            sfl.addFeeRecord(student, toUpdate, semServices.getCurrentActive(), addToBalance);
                        });
                    });
            }
        }
        
        return true;
    }
    
    public boolean deleteRequiredPayment(String requiredPaymentName){
        reqPaymentsRepo.deleteRequiredPaymentByName(requiredPaymentName);
        return true;
    }

    private void runMe(Runnable method){
        CompletableFuture.runAsync(method).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }
}
