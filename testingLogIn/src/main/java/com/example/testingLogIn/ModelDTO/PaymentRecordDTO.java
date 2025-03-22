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
    private String transactionId;
    private String transactionReference;
    private String receivedBy;
    private String studentName;
    private LocalDate date;
    private String SYSemester;
    private double totalAmount;
}
