package com.example.testingLogIn.Services;

import com.example.testingLogIn.ModelDTO.PaymentRecordDTO;
import com.example.testingLogIn.Models.PaymentRecords;
import com.example.testingLogIn.Models.Student;
import com.example.testingLogIn.Repositories.PaymentsRecordRepo;
import com.example.testingLogIn.Repositories.RequiredPaymentsRepo;
import com.example.testingLogIn.Repositories.StudentRepo;
import com.example.testingLogIn.Repositories.sySemesterRepo;
import com.example.testingLogIn.WebsiteSecurityConfiguration.CustomUserDetailsService;
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
    private final PaymentsRecordRepo paymentRepo;
    private final StudentRepo studentRepo;
    private final RequiredPaymentsRepo reqPaymentsRepo;
    private final sySemesterRepo SYSemRepo;
    private final EnrollmentServices enrollmentCheck;
    private final CustomUserDetailsService userService;

    @Autowired
    public PaymentRecordService(PaymentsRecordRepo paymentRepo, StudentRepo studentRepo, RequiredPaymentsRepo reqPaymentsRepo, sySemesterRepo SYSemRepo, EnrollmentServices enrollmentCheck, CustomUserDetailsService userService) {
        this.paymentRepo = paymentRepo;
        this.studentRepo = studentRepo;
        this.reqPaymentsRepo = reqPaymentsRepo;
        this.SYSemRepo = SYSemRepo;
        this.enrollmentCheck = enrollmentCheck;
        this.userService = userService;
    }
    
    public boolean addNewRecord(PaymentRecordDTO paymentRec){
        Student student = studentRepo.findById(paymentRec.getStudId()).orElse(null);
        if(student == null)
            return false;
        
        PaymentRecords newPaymentRecord = PaymentRecords.builder()
                                                    .student(student)
                                                    .receiver(userService.getCurrentlyLoggedInUser())
                                                    .requiredPayment(reqPaymentsRepo.findById(paymentRec.getRequiredPaymentId()).orElse(null))
                                                    .SYSem(SYSemRepo.findCurrentActive())
                                                    .amount(paymentRec.getAmount())
                                                    .build();
        paymentRepo.save(newPaymentRecord);
        enrollmentCheck.updatepcs(student.getStudentId());
        student.setStudentBalance(student.getStudentBalance()-paymentRec.getAmount());
        studentRepo.save(student);
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
    
    public List<PaymentRecordDTO> getAllPaymentRecords(){
        return paymentRepo.getAllRecords().stream()
                        .map(PaymentRecords::DTOmapper)
                        .toList();
    }
    
    public List<PaymentRecordDTO> getAllPaymentRecordsBySem(int semId){
        return paymentRepo.getRecordsBySem(semId).stream()
                        .map(PaymentRecords::DTOmapper)
                        .toList();
    }
    public List<PaymentRecordDTO> getAllTimeRecordsByDate(String condition){
        Sort sort = condition.equalsIgnoreCase("DESC") ? Sort.by(Sort.Order.desc("datePaid")) : Sort.by(Sort.Order.asc("datePaid"));
        return paymentRepo.findAll(sort).stream()
                            .map(PaymentRecords::DTOmapper)
                            .toList();
    }
    public List<PaymentRecordDTO> getAllStudentPaymentRecords(int studentId){
        return paymentRepo.getAllStudentPaymentRecord(studentId).stream()
                            .map(PaymentRecords::DTOmapper)
                            .toList();
    }
    
    public List<PaymentRecordDTO> getAllStudentPaymentRecordsByName(String studentName){
        try{
            Student student = studentRepo.findByStudentDisplayId(studentName);
            return paymentRepo.getAllStudentPaymentRecord(student.getStudentId()).stream()
                                .map(PaymentRecords::DTOmapper)
                                .toList();
        }catch(NullPointerException npe){
            return null;
        }
    }
    
}
