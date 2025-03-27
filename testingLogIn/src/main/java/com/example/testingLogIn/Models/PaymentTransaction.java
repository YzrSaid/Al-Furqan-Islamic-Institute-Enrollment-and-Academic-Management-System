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

    @Builder.Default
    private LocalDate dateReceived = LocalDate.now();
    private double totalAmount;
    private boolean isNotVoided;

    public PaymentTransactionDTO DTOmapper(){
        PaymentTransactionDTO paymentTransactionDTO = PaymentTransactionDTO.builder()
                .studentDisplayId(student.getStudentDisplayId())
                .transactionId(transactionId)
                .transactionReference(transactionId)
                .receivedBy(staff.getFullName())
                .studentName(student.getFullName())
                .date(dateReceived)
                .SYSemester(SYSem.toString())
                .totalAmount(totalAmount)
                .particulars(new ArrayList<>())
                .build();
        for(PaymentRecords pr :particulars)
            paymentTransactionDTO.addNewParticular(pr.getRequiredPayment().getName(),pr.getAmount());

        return paymentTransactionDTO;
    }
}
