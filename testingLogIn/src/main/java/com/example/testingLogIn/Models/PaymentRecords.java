package com.example.testingLogIn.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;

/**
 *
 * @author magno
 */
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
    private RequiredPayments requiredPayment;
    
    private double amount;
    private final LocalDate datePaid = LocalDate.now();
}
