package com.example.testingLogIn.ModelDTO;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
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

    private String transactionReference;
    private String receivedBy;
    private StudentDTO student;
    private LocalDate date;
    private String SYSemester;
    private double totalAmount;
    private List<Particular> particulars;

    public void addNewParticular(String feeName, double amountPaid){
        particulars.add(new Particular(feeName,amountPaid));
    }
}
