package com.example.testingLogIn.ModelDTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
class Particular{
    private String feeName;
    private double amountPaid;
    private double discount;
    private double requiredAmount;
    private double balance;

    public Particular(String feeName, double amountPaid, double discount, double requiredAmount, double balance) {
        this.feeName = feeName;
        this.amountPaid = amountPaid;
        this.discount = discount;
        this.requiredAmount = requiredAmount;
        this.balance = balance;
    }
}

@Builder
@Data
public class PaymentTransactionDTO {
    private String transactionId;
    private String transactionReference;
    private String receivedBy;
    private String studentDisplayId;
    private String studentName;
    private LocalDate date;
    private double remainingBalance;
    private String SYSemester;
    private double totalAmount;
    private List<Particular> particulars;

    public void addNewParticular(String feeName, double amountPaid, double discount, double req, double balance){
        particulars.add(new Particular(feeName,amountPaid,discount,req,balance));
    }
}
