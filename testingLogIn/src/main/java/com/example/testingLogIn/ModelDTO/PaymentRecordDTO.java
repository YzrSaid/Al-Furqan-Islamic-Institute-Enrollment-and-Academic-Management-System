package com.example.testingLogIn.ModelDTO;

import java.time.LocalDate;
import lombok.*;
/**
 *
 * @author magno
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRecordDTO {
    private int recordId;
    
    private int studentId;
    private String studentFirstName;
    private String studentLastName;
    private String studentMiddleName;
    
    private int requiredPaymentId;
    private String requiredPaymentName;
    private String schoolYearAndSemester;
    
    private String staffReceiverName;
    
    private double amount;
    private LocalDate datePaid;
}
