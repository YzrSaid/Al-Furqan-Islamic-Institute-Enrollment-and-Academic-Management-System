package com.example.testingLogIn.Services;

import com.example.testingLogIn.ModelDTO.RequiredPaymentsDTO;
import com.example.testingLogIn.Models.GradeLevelToRequiredPayment;
import com.example.testingLogIn.Models.PaymentCompleteCheck;
import com.example.testingLogIn.Models.PaymentRecords;
import com.example.testingLogIn.Models.SchoolYearSemester;
import com.example.testingLogIn.Models.Student;
import com.example.testingLogIn.Repositories.GradeLevelRequiredFeeRepo;
import com.example.testingLogIn.Repositories.PaymentCheckerRepo;
import com.example.testingLogIn.Repositories.PaymentsRecordRepo;
import com.example.testingLogIn.Repositories.StudentRepo;
import com.example.testingLogIn.Repositories.sySemesterRepo;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 *
 * @author magno
 */
@Service
public class PaymentCheckerService {
    private final sySemesterRepo semRepo;
    private final PaymentCheckerRepo checkerRepo;
    private final StudentRepo studRepo;
    private final PaymentsRecordRepo paymentRecordRepo;
    private final RequiredPaymentsServices reqPaymentService;
    private final GradeLevelRequiredFeeRepo reqFeeGradelvlRepo;

    @Autowired
    public PaymentCheckerService(sySemesterRepo semRepo, PaymentCheckerRepo checkerRepo, StudentRepo studRepo, PaymentsRecordRepo paymentRecordRepo, 
                                RequiredPaymentsServices reqPaymentService, GradeLevelRequiredFeeRepo reqFeeGradelvlRepo) {
        this.semRepo = semRepo;
        this.checkerRepo = checkerRepo;
        this.studRepo = studRepo;
        this.paymentRecordRepo = paymentRecordRepo;
        this.reqPaymentService = reqPaymentService;
        this.reqFeeGradelvlRepo = reqFeeGradelvlRepo;
    }
    
    public PaymentCompleteCheck addIfNotExistElseUpdate(int studentId,int gradeLevelId){
        Student student = studRepo.findById(studentId).orElse(null);
        SchoolYearSemester sysem = semRepo.findCurrentActive();
        PaymentCompleteCheck current = checkerRepo.getCurrent(studentId, sysem.getSySemNumber());
        
        if(!student.isScholar() && current != null){
            boolean isComplete = true;
            List<PaymentRecords> records = paymentRecordRepo.getCurrentPaidRequiredPaymentOfStudent(studentId, sysem.getSySemNumber());
            List<GradeLevelToRequiredPayment> toPayList = reqFeeGradelvlRepo.findByGradeLevel(gradeLevelId);
            Set<Integer> paidRecorded = new HashSet<>();
            for(PaymentRecords pr : records){
                paidRecorded.add(pr.getRequiredPayment().getId());
            }
            for(GradeLevelToRequiredPayment topay: toPayList){
                if(!paidRecorded.contains(topay.getRequiredFee().getId())){
                    isComplete = false;
                    break;}
            }
            current.setComplete(isComplete);
            
            checkerRepo.save(current);
        }else{
            PaymentCompleteCheck checker = new PaymentCompleteCheck();
            checker.setSem(sysem);
            checker.setStudent(student);
            checker.setComplete(false);
            if(student.isScholar())
                checker.setComplete(true);
            
            checkerRepo.save(checker);
        }
        
        return checkerRepo.getCurrent(studentId, studentId);
    }
}
