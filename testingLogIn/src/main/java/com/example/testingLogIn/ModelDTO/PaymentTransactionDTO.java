package com.example.testingLogIn.ModelDTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
class Particular{
    private String feeName;
    private double amountPaid;

    public Particular(String feeName, double amountPaid) {
        this.feeName = feeName;
        this.amountPaid = amountPaid;
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
    private String SYSemester;
    private double totalAmount;
    private List<Particular> particulars;

    public void addNewParticular(String feeName, double amountPaid){
        particulars.add(new Particular(feeName,amountPaid));
    }
}
