package com.example.testingLogIn.Services;

import com.example.testingLogIn.AssociativeModels.StudentTransfereeRequirements;
import com.example.testingLogIn.Models.Student;
import com.example.testingLogIn.Models.TransfereeRequirements;
import com.example.testingLogIn.Repositories.StudentRepo;
import com.example.testingLogIn.Repositories.StudentTransReqRepo;
import com.example.testingLogIn.Repositories.TransfereeReqRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransferReqServices {

    private final TransfereeReqRepo transfereeReqRepo;
    private final StudentTransReqRepo studentTransReqRepo;
    private final StudentRepo studentRepo;

    @Autowired
    public TransferReqServices(TransfereeReqRepo transfereeReqRepo, StudentTransReqRepo studentTransReqRepo, StudentRepo studentRepo) {
        this.transfereeReqRepo = transfereeReqRepo;
        this.studentTransReqRepo = studentTransReqRepo;
        this.studentRepo = studentRepo;
    }

    //                  for manipulating transferee requirements
    public List<TransfereeRequirements> getAllRequirements(boolean isNotDeleted){
        if(isNotDeleted)
            return transfereeReqRepo.findByIsNotDeletedTrue();
        return transfereeReqRepo.findByIsNotDeletedFalse();
    }
    public int addNewRequirement(String requirementName){
        if(transfereeReqRepo.findUsingName("%"+requirementName.toLowerCase()+"%").orElse(null) == null){
            transfereeReqRepo.save(TransfereeRequirements.builder()
                            .name(requirementName)
                            .isNotDeleted(true)
                            .build());
            return 0;
        }

        return 1;
    }
    public boolean deleteRequirement(int requirementId){
        TransfereeRequirements req = transfereeReqRepo.findById(requirementId).orElseThrow(NullPointerException::new);
        req.setNotDeleted(false);
        transfereeReqRepo.save(req);
        return true;
    }

    public boolean updateName(int requirementId,String newName){
        TransfereeRequirements existingReq = transfereeReqRepo.findUsingName("%"+newName.toLowerCase()+"%").orElse(null);
        TransfereeRequirements toUpdate = transfereeReqRepo.findById(requirementId).orElseThrow(NullPointerException::new);
        if(existingReq != null && existingReq.getId() != toUpdate.getId())
            return false;

        toUpdate.setName(newName);
        transfereeReqRepo.save(toUpdate);
        return true;
    }

    // for manipulating the requirements complied by the transferee student
    public boolean addingStudentRequirements(int studentId, List<Integer> requirementsId){
        Student student = studentRepo.findById(studentId).orElseThrow(NullPointerException::new);
        List<TransfereeRequirements> transfereeRequirements = transfereeReqRepo.findByIsNotDeletedTrue();
        transfereeRequirements.forEach(requirement ->{
            StudentTransfereeRequirements studReq = studentTransReqRepo.findStudentRecord(studentId,requirement.getId()).orElse(new StudentTransfereeRequirements(student,requirement));
            studReq.setNotDeleted(requirementsId.contains(requirement.getId()));
            studentTransReqRepo.save(studReq);
        });
        return true;
    }

}
