package com.example.testingLogIn.Models;

import com.example.testingLogIn.ModelDTO.PaymentRecordDTO;
import com.example.testingLogIn.WebsiteSecurityConfiguration.UserModel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 *
 * @author magno
 */
@Builder
@Getter
@Setter
@ToString
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
