package com.example.testingLogIn.Models;

import com.example.testingLogIn.ModelDTO.PaymentRecordDTO;
import com.example.testingLogIn.WebsiteSecurityConfiguration.UserModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JoinColumn(name = "transaction",nullable = false)
    @JsonIgnore
    private PaymentTransaction transaction;
    
    @ManyToOne
    @JoinColumn(name = "requiredPayment")
    private RequiredFees requiredPayment;

    private double amount;
    
    public PaymentRecordDTO DTOmapper(){
        return PaymentRecordDTO.builder()
                            .transactionId(transaction.getTransactionId())
                            .transactionReference(requiredPayment.getName())
                            .studentName(transaction.getStudent().getFullName())
                            .receivedBy(transaction.getStaff().getFullName())
                            .date(transaction.getDateReceived())
                            .totalAmount(amount)
                            .build();
    }
}
