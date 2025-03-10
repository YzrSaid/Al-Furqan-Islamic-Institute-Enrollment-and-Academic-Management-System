package com.example.testingLogIn.Services;

import com.example.testingLogIn.ModelDTO.GradeLevelToRequiredPaymentDTO;
import com.example.testingLogIn.ModelDTO.RequiredPaymentsDTO;
import com.example.testingLogIn.Models.GradeLevel;
import com.example.testingLogIn.Models.GradeLevelToRequiredPayment;
import com.example.testingLogIn.Models.RequiredFees;
import com.example.testingLogIn.Repositories.GradeLevelRequiredFeeRepo;
import com.example.testingLogIn.Repositories.RequiredPaymentsRepo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    public RequiredPaymentsServices(RequiredPaymentsRepo reqPaymentsRepo, GradeLevelServices gradeLevelService,GradeLevelRequiredFeeRepo reqFeeGradelvlRepo) {
        this.reqPaymentsRepo = reqPaymentsRepo;
        this.gradeLevelService = gradeLevelService;
        this.reqFeeGradelvlRepo = reqFeeGradelvlRepo;
    }
    
    public RequiredFees getRequiredPaymentById(int reqPaymentId){
        return reqPaymentsRepo.findById(reqPaymentId).orElse(null);
    }
    
    public boolean addNewPayments(RequiredPaymentsDTO paymentsDTO){
        boolean doesNameExist = reqPaymentsRepo.findAll().stream()
                                                .filter(payment -> payment.isNotDeleted() && 
                                                                    payment.getName().equalsIgnoreCase(paymentsDTO.getName()))
                                                .toList().isEmpty();
        if(!doesNameExist)
            return false;
        else{
            RequiredFees reqFee = RequiredFees.builder()
                                            .name(paymentsDTO.getName())
                                            .isNotDeleted(true)
                                            .requiredAmount(paymentsDTO.getRequiredAmount())
                                            .build();
            reqPaymentsRepo.save(reqFee);
            RequiredFees newFee = reqPaymentsRepo.findAll().stream()
                                                .filter(reqPayment -> reqPayment.isNotDeleted() && 
                                                                        reqPayment.getName().equalsIgnoreCase(paymentsDTO.getName()))
                                                .findFirst().orElse(null);
            
            List<GradeLevel> gradeLevels = gradeLevelService.getAllGradeLevels().stream()
                                                        .filter(gradelvl -> paymentsDTO.getGradeLevelNames().contains(gradelvl.getLevelName()))
                                                        .toList();
            gradeLevels
                    .forEach(gradelvl ->{
                        GradeLevelToRequiredPayment newPayment = GradeLevelToRequiredPayment.builder()
                                                    .requiredFee(newFee)
                                                    .isNotDeleted(true)
                                                    .gradeLevel(gradelvl)
                                                    .build();
                        reqFeeGradelvlRepo.save(newPayment);
                    });
        }
        
        return true;
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
                                                        .gradeLevelNames(new ArrayList<String>())
                                                        .build();
                reqFeeGradelvlRepo.findByRequiredFee(payment.getId()).stream()
                        .filter(GradeLevelToRequiredPayment::isNotDeleted)
                        .forEach(gradelvl -> {
                            paymentDTO.getGradeLevelNames().add(gradelvl.getGradeLevel().getLevelName());
                        });
                uniquePayments.add(paymentDTO);
        }
        
        return uniquePayments;
    }
    
    public List<GradeLevelToRequiredPaymentDTO> getPaymentsByGradeLevel(int gradeLevelId){
        return reqFeeGradelvlRepo.findByRequiredFee(gradeLevelId).stream()
                        .filter(GradeLevelToRequiredPayment::isNotDeleted)
                        .map(GradeLevelToRequiredPayment::DTOmapper)
                        .toList();
    }
    
    public boolean updatePayment(int feeId, RequiredPaymentsDTO updated){
        RequiredFees toUpdate = reqPaymentsRepo.findById(feeId).orElse(null);
        assert toUpdate != null;
        toUpdate.setName(updated.getName());
        toUpdate.setRequiredAmount(updated.getRequiredAmount());
        reqPaymentsRepo.save(toUpdate);

        List<GradeLevelToRequiredPayment> affectedRows = reqFeeGradelvlRepo.findByRequiredFee(feeId);
        
        for(GradeLevelToRequiredPayment payment : affectedRows){
            String toRemoveGrade = payment.getGradeLevel().getLevelName();
            if(!updated.getGradeLevelNames().contains(toRemoveGrade))
                payment.setNotDeleted(false);
            reqFeeGradelvlRepo.save(payment);
            updated.getGradeLevelNames().remove(toRemoveGrade);
        }
        
        if(!updated.getGradeLevelNames().isEmpty()){
            for(String gradeLevelName : updated.getGradeLevelNames()){
                GradeLevel gradeLevel = gradeLevelService.getByName(gradeLevelName);
                GradeLevelToRequiredPayment newReqPayment = GradeLevelToRequiredPayment.builder()
                                                                .requiredFee(toUpdate)
                                                                .gradeLevel(gradeLevel)
                                                                .isNotDeleted(true)
                                                                .build();
                reqFeeGradelvlRepo.save(newReqPayment);
            }
        }
        
        return true;
    }
    
    public boolean deleteRequiredPayment(String requiredPaymentName){
        reqPaymentsRepo.deleteRequiredPaymentByName(requiredPaymentName);
        return true;
    }
    
    private RequiredPaymentsDTO partialMapper(RequiredFees requiredPayment){
        return RequiredPaymentsDTO.builder()
                                .id(requiredPayment.getId())
                                .name(requiredPayment.getName())
                                .requiredAmount(requiredPayment.getRequiredAmount())
                                .build();
    }
}
