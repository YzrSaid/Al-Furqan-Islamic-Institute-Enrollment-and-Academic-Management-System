package com.example.testingLogIn.Models;

import com.example.testingLogIn.ModelDTO.PaymentRecordDTO;
import com.example.testingLogIn.WebsiteSecurityConfiguration.UserModel;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.*;

/**
 *
 * @author magno
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PaymentRecords {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int recordId;
    
    @ManyToOne
    @JoinColumn(name = "student")
    private Student student;
    
    @ManyToOne
    @JoinColumn(name = "requiredPayment")
    private RequiredFees requiredPayment;
    
    @ManyToOne
    @JoinColumn(name = "semester")
    private SchoolYearSemester SYSem;
    
    @ManyToOne
    @JoinColumn(name = "receivedBy")
    private UserModel receiver;
    
    private double amount;
    private final LocalDate datePaid = LocalDate.now();
    
    public PaymentRecordDTO DTOmapper(){
        return PaymentRecordDTO.builder()
                            .recordId(recordId)
                
                            .studId(student.getStudentId())
                            .studentId(student.getStudentDisplayId())
                            .studentFirstName(student.getFirstName())
                            .studentLastName(student.getLastName())
                            .studentMiddleName(student.getMiddleName())
                            .amount(amount)
                            .datePaid(datePaid)
                            
                            .requiredPaymentId(requiredPayment.getId())
                            .requiredPaymentName(requiredPayment.getName())
                            
                            .staffReceiverName(receiver.getFirstname()+" "+receiver.getLastname())
                
                            .schoolYearAndSemester(SYSem.getSchoolYear().getSchoolYear()+" : "+SYSem.getSem())
                            .build();
    }
}
