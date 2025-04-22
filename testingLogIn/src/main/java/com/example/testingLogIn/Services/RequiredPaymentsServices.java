package com.example.testingLogIn.Services;

import com.example.testingLogIn.AssociativeModels.StudentFeesList;
import com.example.testingLogIn.CustomObjects.StudentTotalDiscount;
import com.example.testingLogIn.ModelDTO.DistributableDTO;
import com.example.testingLogIn.ModelDTO.GradeLevelToRequiredPaymentDTO;
import com.example.testingLogIn.ModelDTO.RequiredPaymentsDTO;
import com.example.testingLogIn.Models.GradeLevel;
import com.example.testingLogIn.AssociativeModels.GradeLevelRequiredFees;
import com.example.testingLogIn.Models.RequiredFees;
import com.example.testingLogIn.Models.SchoolYearSemester;
import com.example.testingLogIn.Models.Student;
import com.example.testingLogIn.Repositories.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
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
    private StudentFeesListRepo sflRepo;

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

    @CacheEvict(value = "enrollmentPage",allEntries = true)
    public int addNewPayments(RequiredPaymentsDTO paymentsDTO) throws Exception {
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
                                            .isCurrentlyActive(paymentsDTO.isWillApplyNow())
                                            .requiredAmount(paymentsDTO.getRequiredAmount())
                                            .build();
            RequiredFees newFee = reqPaymentsRepo.save(reqFee);
            List<GradeLevel> gradeLevels = gradeLevelService.getAllGradeLevels().stream()
                                                        .filter(gradelvl -> paymentsDTO.getGradeLevels().contains(gradelvl.getLevelId()) && gradelvl.isNotDeleted())
                                                        .toList();

            gradeLevels.forEach(gradelvl -> reqFeeGradelvlRepo.save(GradeLevelRequiredFees.build(gradelvl,newFee)));
            if (paymentsDTO.isWillApplyNow())
                runMe(() -> {
                    List<Student> studentList = new ArrayList<>();
                    List<StudentFeesList> studentFeesListList = new ArrayList<>();
                    SchoolYearSemester currentSem = semServices.getCurrentActive();
                    if(currentSem != null){
                        gradeLevels.forEach(gradelvl -> studentList.addAll(enrollmentRepo.getCurrentlyEnrolledToGrade(gradelvl.getLevelId(), currentSem.getSySemNumber())));
                        studentList.forEach(student -> {
                            double addToBalance = NonModelServices.zeroIfLess(afterDiscount(student,newFee.getRequiredAmount()));
                            student.setStudentBalance(student.getStudentBalance() + addToBalance);
                            studentFeesListList.add(StudentFeesList.build(newFee,currentSem,student,addToBalance));
                        });
                        if(!studentList.isEmpty())
                            studentRepo.saveAll(studentList);
                        if(!studentFeesListList.isEmpty())
                            sfl.addFeeRecordList(studentFeesListList);
                    }
                });

            if(paymentsDTO.isDistributable()){
                try{
                    if(!distributableServices.addNewDistributable(DistributableDTO.builder()
                                    .gradeLevelIds(gradeLevels.stream().map(GradeLevel::getLevelId).toList())
                                    .itemName(paymentsDTO.getName())
                                    .isCurrentlyActive(paymentsDTO.isWillApplyNow())
                            .build()));

                }catch (NullPointerException npe) {}
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
                                                        .isCurrentlyActive(payment.isCurrentlyActive())
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

    @CacheEvict(value = "enrollmentPage",allEntries = true)
    public boolean updatePayment(int feeId, RequiredPaymentsDTO updated){
        RequiredFees toUpdate = reqPaymentsRepo.findById(feeId).orElse(null);
        assert toUpdate != null;
        boolean isActiveBefore = toUpdate.isCurrentlyActive();
        toUpdate.setCurrentlyActive(updated.isWillApplyNow());
        toUpdate.setName(updated.getName());
        toUpdate.setRequiredAmount(updated.getRequiredAmount());
        reqPaymentsRepo.save(toUpdate);

        SchoolYearSemester currentSem = semServices.getCurrentActive();
        int currentSemId = Optional.ofNullable(currentSem).map(SchoolYearSemester::getSySemNumber).orElse(0);
        List<GradeLevelRequiredFees> valid = new ArrayList<>();
        List<GradeLevelRequiredFees> affectedRows = reqFeeGradelvlRepo.findByRequiredFee(feeId);
        for(GradeLevelRequiredFees payment : affectedRows){
            int levelId = payment.getGradeLevel().getLevelId();
            payment.setNotDeleted(true);
            if(!updated.getGradeLevels().contains(levelId))
                payment.setNotDeleted(false);

            if(payment.isNotDeleted())
                valid.add(payment);

            runMe(()->{
                List<StudentFeesList> updatedStudFee = new ArrayList<>();
                sflRepo.findEnrolledStudentsWithRecord(levelId, currentSemId, feeId)
                        .forEach(student -> {
                            StudentFeesList studFee = sfl.studentFeesList(student.getStudentId(), feeId, currentSemId);
                            if (studFee != null) {
                                double newBalToPay;
                                if (payment.isNotDeleted() && toUpdate.isCurrentlyActive()) {
                                    newBalToPay =  NonModelServices.zeroIfLess(afterDiscount(student,toUpdate.getRequiredAmount()));
                                    student.setStudentBalance(student.getStudentBalance() + (newBalToPay - studFee.getAmount()));
                                } else {
                                    student.setStudentBalance(student.getStudentBalance() - studFee.getAmount());
                                    newBalToPay = 0.0d;
                                }
                                studFee.setAmount(newBalToPay);
                                updatedStudFee.add(studFee);
                                studentRepo.save(student);
                            }
                        });
                if (!updatedStudFee.isEmpty())
                    sfl.updateFeeRecord(updatedStudFee);
            });
            reqFeeGradelvlRepo.save(payment);
            updated.getGradeLevels().remove(Integer.valueOf(levelId));
        }

        updated.getGradeLevels().forEach(lvlIds ->{
            GradeLevel gradeLevel = gradeLevelService.getGradeLevel(lvlIds);
            valid.add(reqFeeGradelvlRepo.save(GradeLevelRequiredFees.build(gradeLevel,toUpdate)));
        });
        List<Student> studentNoFeeRecord = new ArrayList<>();
        valid.forEach(glr->{
            studentNoFeeRecord.addAll(
                    sflRepo.findEnrolledStudentsNoRecord(glr.getGradeLevel().getLevelId(),currentSemId,glr.getRequiredFee().getId()));
        });

        if (toUpdate.isCurrentlyActive()){
            runMe(() -> {
                List<StudentFeesList> newStudentFeesList = new ArrayList<>();
                studentNoFeeRecord.forEach(student -> {
                    double addToBalance =  NonModelServices.zeroIfLess(afterDiscount(student,toUpdate.getRequiredAmount()));
                    student.setStudentBalance(student.getStudentBalance() + addToBalance);
                    newStudentFeesList.add(StudentFeesList.build(toUpdate,currentSem,student,addToBalance));
                });
                if(!newStudentFeesList.isEmpty())
                    sfl.addFeeRecordList(newStudentFeesList);
                if(!studentNoFeeRecord.isEmpty())
                    studentRepo.saveAll(studentNoFeeRecord);
            });
        }
        return true;
    }

    public double afterDiscount(Student student, double initialPrice){
        StudentTotalDiscount std = discService.getStudentTotalDiscount(student.getStudentId());
        return (initialPrice - NonModelServices.adjustDecimal(((initialPrice * std.getTotalPercentageDiscount()) + std.getTotalFixedDiscount())));
    }

    @CacheEvict(value = "enrollmentPage",allEntries = true)
    public void deleteRequiredPayment(int feeId){
        RequiredFees reqFee = reqPaymentsRepo.findById(feeId).orElseThrow(NullPointerException::new);
        int currentSemId = Optional.ofNullable(semServices.getCurrentActive()).map(SchoolYearSemester::getSySemNumber).orElse(0);
        runMe(()-> reqFeeGradelvlRepo.setAsDeleted(feeId));
        if(reqFee.isCurrentlyActive() && currentSemId > 0)
            runMe(()->{
                    List<Student> updatedStudents = new ArrayList<>();
                    List<StudentFeesList> studFeeList = sfl.feesList(reqFee.getId(),currentSemId);
                    studFeeList.forEach(studFee ->{
                        Student student = studFee.getStudent();
                        student.setStudentBalance(student.getStudentBalance() - studFee.getAmount());
                        studFee.setAmount(0.0d);
                        updatedStudents.add(student);
                    });
                    if(!updatedStudents.isEmpty())
                        studentRepo.saveAll(updatedStudents);
                    if(!studFeeList.isEmpty())
                        sfl.updateFeeRecord(studFeeList);}
            );
        reqFee.setNotDeleted(false);
        reqPaymentsRepo.save(reqFee);
    }

    private void runMe(Runnable method){
        CompletableFuture.runAsync(method).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }
}
