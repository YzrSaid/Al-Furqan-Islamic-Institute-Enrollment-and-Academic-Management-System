package com.example.testingLogIn.Services;

import com.example.testingLogIn.CustomObjects.StudentTotalDiscount;
import com.example.testingLogIn.ModelDTO.RequiredPaymentsDTO;
import com.example.testingLogIn.Models.Enrollment;
import com.example.testingLogIn.AssociativeModels.StudentFeesList;
import com.example.testingLogIn.Models.RequiredFees;
import com.example.testingLogIn.Models.SchoolYearSemester;
import com.example.testingLogIn.Models.Student;
import com.example.testingLogIn.Repositories.GradeLevelRequiredFeeRepo;
import com.example.testingLogIn.Repositories.StudentFeesListRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudentFeesListService {
    @Autowired
    private StudentFeesListRepo studFeeRepo;
    @Autowired
    private GradeLevelRequiredFeeRepo gradeReqFeeRepo;
    @Autowired
    private DiscountsServices discService;;
    @Async
    public void addFeesRecord(Enrollment e){
        StudentTotalDiscount std = discService.getStudentTotalDiscount(e.getStudent().getStudentId());
        gradeReqFeeRepo.findByGradeLevel(e.getGradeLevelToEnroll().getLevelId())
                .forEach(reqFee -> {
                    double reqAmount = reqFee.getRequiredFee().getRequiredAmount();
                    double discountedAmount = Math.ceil(reqAmount - ((reqAmount*std.getTotalPercentageDiscount()) + std.getTotalFixedDiscount()));
                    studFeeRepo.save(StudentFeesList.build(reqFee.getRequiredFee(),e.getSYSemester(),e.getStudent(),discountedAmount));
                });
    }

    @Async
    public void addFeeRecord(Student student, RequiredFees fee, SchoolYearSemester sem, double amount){
        studFeeRepo.save(StudentFeesList.build(fee,sem,student,amount));
    }

    @Async
    public void updateFeeRecord(StudentFeesList studentFeesList){
        studFeeRepo.save(studentFeesList);
    }

    public List<RequiredPaymentsDTO> feesList(int studentId,Integer semNumber){
        List<StudentFeesList> feesList = studFeeRepo.findBySem(studentId,semNumber);
        List<RequiredPaymentsDTO> reqFeeList = new ArrayList<>();

        feesList.forEach(fee -> reqFeeList.add(RequiredPaymentsDTO.builder()
                .name(fee.getFee().getName())
                .requiredAmount(fee.getFee().getRequiredAmount())
                .build()));
        return reqFeeList;
    }

    protected StudentFeesList studentFeesList(int studentId,int feeId,int semId){
        return studFeeRepo.getFeesBySem(studentId,feeId,semId).orElse(null);
    }
}
