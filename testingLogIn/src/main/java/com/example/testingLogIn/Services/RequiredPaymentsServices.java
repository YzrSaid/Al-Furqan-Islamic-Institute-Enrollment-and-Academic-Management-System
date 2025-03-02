/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.testingLogIn.Services;

import com.example.testingLogIn.Enums.EnrollmentStatus;
import com.example.testingLogIn.ModelDTO.RequiredPaymentsDTO;
import com.example.testingLogIn.Models.GradeLevel;
import com.example.testingLogIn.Models.RequiredPayments;
import com.example.testingLogIn.Repositories.RequiredPaymentsRepo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 *
 * @author magno
 */
@Service
public class RequiredPaymentsServices {
    private final RequiredPaymentsRepo reqPaymentsRepo;
    private final GradeLevelServices gradeLevelService;

    public RequiredPaymentsServices(RequiredPaymentsRepo reqPaymentsRepo, GradeLevelServices gradeLevelService) {
        this.reqPaymentsRepo = reqPaymentsRepo;
        this.gradeLevelService = gradeLevelService;
    }
    
    public boolean addNewPayments(RequiredPaymentsDTO paymentsDTO){
        boolean doesNameExist = reqPaymentsRepo.findAll().stream()
                                                .filter(payment -> payment.isNotDeleted() && 
                                                                    payment.getName().equalsIgnoreCase(paymentsDTO.getName()))
                                                .toList().isEmpty();
        if(!doesNameExist)
            return false;
        else{
            List<GradeLevel> gradeLevels = gradeLevelService.getAllGradeLevels().stream()
                                                        .filter(gradelvl -> paymentsDTO.getGradeLevels().contains(gradelvl.getLevelId()))
                                                        .toList();
            gradeLevels
                    .forEach(gradelvl ->{
                        RequiredPayments newPayment = RequiredPayments.builder()
                                                    .name(paymentsDTO.getName())
                                                    .isNotDeleted(true)
                                                    .gradeLevel(gradelvl)
                                                    .requiredAmount(paymentsDTO.getRequiredAmount())
                                                    .build();
                        reqPaymentsRepo.save(newPayment);
                    });
        }
        
        return true;
    }
    
    public Map<String,RequiredPaymentsDTO> getAllPayments(){
        List<RequiredPayments> paymentsList = reqPaymentsRepo.findAll().stream()
                                .filter(payment -> payment.isNotDeleted())
                                .sorted((p1,p2) -> p1.getGradeLevel().getLevelName().compareTo(p2.getGradeLevel().getLevelName()))
                                .toList();
        Map<String,RequiredPaymentsDTO> uniquePayments = new HashMap<>();
        for(RequiredPayments payment : paymentsList){
            String paymentName = payment.getName();
            if(!uniquePayments.containsKey(paymentName)){
                RequiredPaymentsDTO paymentDTO = RequiredPaymentsDTO.builder()
                                                        .name(paymentName)
                                                        .requiredAmount(payment.getRequiredAmount())
                                                        .gradeLevelNames(new ArrayList<String>())
                                                        .build();
                uniquePayments.put(paymentName,paymentDTO);
            }
            uniquePayments.get(paymentName).getGradeLevelNames().add(payment.getGradeLevel().getLevelName());
        }
        
        return uniquePayments;
    }
    
    public List<RequiredPaymentsDTO> getPaymentsByGradeLevel(int gradeLevelId){
        return reqPaymentsRepo.findAll().stream()
                                .filter(payment -> payment.isNotDeleted() && 
                                        payment.getGradeLevel().getLevelId() == gradeLevelId)
                                .map(reqPayment -> partialMapper(reqPayment))
                                .toList();
    }
    
    public double getToPayTotal(int gradeLevelId){
        double total = 0;
        List<RequiredPayments> paymentList = reqPaymentsRepo.findAll().stream()
                                    .filter(payment -> payment.isNotDeleted() && 
                                            payment.getGradeLevel().getLevelId() == gradeLevelId)
                                    .toList();
        for(RequiredPayments payment : paymentList)
            total+=payment.getRequiredAmount();
        
        return total;
    }
    
    public boolean updatePayment(String recentPaymentName, RequiredPaymentsDTO updated){
        List<RequiredPayments> toUpdate = reqPaymentsRepo.findAll().stream()
                                    .filter(payment -> payment.isNotDeleted() && 
                                            payment.getName().equalsIgnoreCase(recentPaymentName))
                                    .toList();
        
        if(toUpdate.isEmpty())
            return false;
        
        for(RequiredPayments payment : toUpdate){
            String toRemoveGrade = payment.getGradeLevel().getLevelName();
            payment.setName(updated.getName());
            payment.setRequiredAmount(updated.getRequiredAmount());
            if(!updated.getGradeLevelNames().contains(payment.getGradeLevel().getLevelName()))
                payment.setNotDeleted(false);
            reqPaymentsRepo.save(payment);
            updated.getGradeLevelNames().remove(toRemoveGrade);
        }
        
        if(!updated.getGradeLevelNames().isEmpty()){
            for(String gradeLevelName : updated.getGradeLevelNames()){
                GradeLevel gradeLevel = gradeLevelService.getByName(gradeLevelName);
                RequiredPayments newReqPayment = RequiredPayments.builder()
                                                                .name(updated.getName())
                                                                .requiredAmount(updated.getRequiredAmount())
                                                                .gradeLevel(gradeLevel)
                                                                .isNotDeleted(true)
                                                                .build();
                reqPaymentsRepo.save(newReqPayment);
            }
        }
        
        return true;
    }
    
    public boolean deleteRequiredPayment(String requiredPaymentName){
        System.out.println(requiredPaymentName);
        reqPaymentsRepo.deleteRequiredPaymentByName(requiredPaymentName);
        return true;
    }
    
    private RequiredPaymentsDTO partialMapper(RequiredPayments requiredPayment){
        return RequiredPaymentsDTO.builder()
                                .id(requiredPayment.getId())
                                .name(requiredPayment.getName())
                                .requiredAmount(requiredPayment.getRequiredAmount())
                                .build();
    }
}
