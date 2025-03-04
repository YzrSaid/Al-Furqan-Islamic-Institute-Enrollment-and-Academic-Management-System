package com.example.testingLogIn.Services;

import com.example.testingLogIn.ModelDTO.PaymentRecordDTO;
import com.example.testingLogIn.Models.PaymentRecords;
import com.example.testingLogIn.Models.Student;
import com.example.testingLogIn.Repositories.PaymentsRecordRepo;
import com.example.testingLogIn.Repositories.RequiredPaymentsRepo;
import com.example.testingLogIn.Repositories.StudentRepo;
import com.example.testingLogIn.Repositories.sySemesterRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author magno
 */
@Service
public class PaymentRecordService {
    @Autowired
    private PaymentsRecordRepo paymentRepo;
    @Autowired
    private StudentRepo studentRepo;
    @Autowired
    private RequiredPaymentsRepo reqPaymentsRepo;
    @Autowired
    private sySemesterRepo SYSemRepo;
    @Autowired
    private PaymentCheckerService checkerService;
    
    public boolean addNewRecord(PaymentRecordDTO paymentRec){
        Student student = studentRepo.findById(paymentRec.getStudentId()).orElse(null);
        PaymentRecords newPaymentRecord = PaymentRecords.builder()
                                                    .student(student)
                                                    .requiredPayment(reqPaymentsRepo.findById(paymentRec.getRequiredPaymentId()).orElse(null))
                                                    .SYSem(SYSemRepo.findCurrentActive())
                                                    .amount(paymentRec.getAmount())
                                                    .build();
        paymentRepo.save(newPaymentRecord);
        return true;
    }
    
}
