package com.example.testingLogIn.Services;

import com.example.testingLogIn.ModelDTO.PaymentRecordDTO;
import com.example.testingLogIn.Models.PaymentRecords;
import com.example.testingLogIn.Models.Student;
import com.example.testingLogIn.Repositories.PaymentsRecordRepo;
import com.example.testingLogIn.Repositories.RequiredPaymentsRepo;
import com.example.testingLogIn.Repositories.StudentRepo;
import com.example.testingLogIn.Repositories.sySemesterRepo;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;

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
    private EnrollmentServices enrollmentCheck;
    
    public boolean addNewRecord(PaymentRecordDTO paymentRec){
        Student student = studentRepo.findById(paymentRec.getStudentId()).orElse(null);
        PaymentRecords newPaymentRecord = PaymentRecords.builder()
                                                    .student(student)
                                                    .requiredPayment(reqPaymentsRepo.findById(paymentRec.getRequiredPaymentId()).orElse(null))
                                                    .SYSem(SYSemRepo.findCurrentActive())
                                                    .amount(paymentRec.getAmount())
                                                    .build();
        paymentRepo.save(newPaymentRecord);
        enrollmentCheck.updatepcs(student.getStudentId());
        return true;
    }
    
    public boolean editRecord(int recordId, int feeId){
        PaymentRecords record = paymentRepo.findById(recordId).orElse(null);
        
        if(record == null)
            return false;
        
        record.setRequiredPayment(reqPaymentsRepo.findById(feeId).orElse(null));
        paymentRepo.save(record);
        enrollmentCheck.updatepcs(record.getStudent().getStudentId());
        return true;
    }
    
    public List<PaymentRecordDTO> getAllPaymentRecordsBySem(int semId){
        return paymentRepo.getRecordsBySem(semId).stream()
                        .map(PaymentRecords::DTOmapper)
                        .toList();
    }
    
    public List<PaymentRecordDTO> getAllTimeRecordsByDate(String condition){
        Sort sort = condition.equalsIgnoreCase("DESC") ? Sort.by(Sort.Order.desc("datePaid")) : Sort.by(Sort.Order.asc("datePaid"));
        return paymentRepo.getAllRecordsSortByDate(sort).stream()
                            .map(PaymentRecords::DTOmapper)
                            .toList();
    }
    
    public List<PaymentRecordDTO> getAllStudentPaymentRecords(int studentId){
        return paymentRepo.getAllStudentPaymentRecord(studentId).stream()
                            .map(PaymentRecords::DTOmapper)
                            .toList();
    }
    
}
