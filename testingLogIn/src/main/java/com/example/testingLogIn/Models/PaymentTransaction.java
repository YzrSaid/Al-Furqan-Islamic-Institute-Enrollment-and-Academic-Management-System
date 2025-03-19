package com.example.testingLogIn.Models;

import com.example.testingLogIn.ModelDTO.PaymentTransactionDTO;
import com.example.testingLogIn.WebsiteSecurityConfiguration.UserModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PaymentTransaction {

    @Id private String transactionId;

    @ManyToOne
    @JoinColumn(name="receiver",nullable = false)
    private UserModel staff;

    @ManyToOne
    @JoinColumn(name = "semester")
    private SchoolYearSemester SYSem;

    @ManyToOne
    @JoinColumn(name = "student")
    private Student student;

    @OneToMany(mappedBy = "transaction")
    private List<PaymentRecords> particulars;

    private LocalDate dateReceived = LocalDate.now();

    public PaymentTransactionDTO DTOmapper(){
        PaymentTransactionDTO transactionDTO = PaymentTransactionDTO.builder()
                .transactionReference(transactionId)
                .receivedBy(staff.getFirstname()+" "+staff.getLastname())
                .student(student.DTOmapper())
                .date(dateReceived)
                .SYSemester(SYSem.toString())
                .particulars(new ArrayList<>())
                .build();
        double total = 0;
        for(PaymentRecords pr :particulars){
            transactionDTO.addNewParticular(pr.getRequiredPayment().getName(),pr.getAmount());
            total+=pr.getAmount();
        }
        transactionDTO.setTotalAmount(total);
        return transactionDTO;
    }
}
